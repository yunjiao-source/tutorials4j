# cache-redis

## 工具类

### `RedisUtils` – 工具接口

提供静态方法生成统一的`CacheKeyPrefix`：

- `tutorials4jCacheKeyPrefix()`：返回`CacheKeyPrefix.prefixed("tutorials4j:cache:")`
- `tutorials4jCacheKeyPrefix(String name)`：返回`CacheKeyPrefix.prefixed("tutorials4j:cache:" + name + ":")`

另外还提供了一个基于类名+方法名+参数列表的`KeyGenerator`实现，可用于`@Cacheable`注解的`keyGenerator`属性。


# 深入剖析 Spring Boot Redis 命名缓存配置：多级定制化缓存管理实践

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-cache/cache-redis

示例代码在https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-examples/examples-cache-redis

## 摘要

在分布式系统中，缓存是提升性能的关键手段。Spring Boot 结合 Redis 提供了开箱即用的缓存支持，但当业务需要为不同缓存区域（如用户信息、商品详情）设置差异化配置（TTL、空值处理、键前缀）时，默认的全局配置往往难以满足需求。本文基于一段生产级代码，深入分析如何通过自定义 `RedisCacheManagerBuilderCustomizer` 与 `CacheManagerCustomizer`，实现**命名缓存（Named Cache）**的动态配置，并整合 Spring Boot 配置属性，达到灵活、可扩展的缓存管理目标。

---

## 一、引言：为何需要命名缓存？

Spring Boot 的 `CacheProperties.Redis` 提供了全局的 Redis 缓存配置，例如统一的过期时间、键前缀等。然而实际业务场景中：

- 验证码缓存需要 **30 秒** 过期，用户会话缓存需要 **1 小时** 过期；
- 部分缓存需**禁止保存 null 值**，避免穿透；
- 不同缓存模块希望有**独立的键名前缀**（如 `auth:`、`product:`）。

若仅依赖全局配置，无法满足上述差异。**命名缓存（Named Cache）**允许为每个缓存名称单独定义配置，Spring Data Redis 的 `RedisCacheManager.Builder` 支持 `withInitialCacheConfigurations(Map<String, RedisCacheConfiguration>)` 方法，这正是本文代码所利用的核心扩展点。

---

## 二、整体设计思路

代码分为四个关键组件：

| 类名 | 角色 |
|------|------|
| `CachesProperties` | 扩展 Spring Boot 配置，增加 `namedCaches` 属性，用于定义各缓存的独立配置 |
| `NamedCacheConfiguration` | Spring 配置类，注册两个定制器 Bean |
| `NamedCacheManagerBuilderCustomizer` | 实现 `RedisCacheManagerBuilderCustomizer`，在 `RedisCacheManager` 构建前注入命名缓存配置 |
| `NamedCacheManagerCustomizer` | 实现 `CacheManagerCustomizer`，强制调用 `initializeCaches()` 确保命名缓存提前初始化 |

整体流程：
1. 从配置文件（如 `application.yml`）读取 `namedCaches` 映射；
2. 在 `RedisCacheManager` 构建过程中，为每个命名缓存生成独立的 `RedisCacheConfiguration`；
3. 调用 `builder.withInitialCacheConfigurations()` 注册；
4. 最后通过 `initializeCaches()` 触发预初始化，避免运行时动态创建缓存时使用默认配置。

---

## 三、关键代码解析

### 3.1 扩展配置属性：CachesProperties

```java
@Data
@ConfigurationProperties(prefix = "tutorials4j.cache")
public class CachesProperties {
    private RedisCacheOptions redis = new RedisCacheOptions();

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class RedisCacheOptions extends CacheProperties.Redis {
        private Map<String, CacheProperties.Redis> namedCaches = new HashMap<>();
    }
}
```

- 继承了 Spring Boot 的 `CacheProperties.Redis`，复用其 `timeToLive`、`cacheNullValues`、`keyPrefix` 等字段。
- 新增 `namedCaches` 属性，key 为缓存名称（如 `users`），value 为针对该缓存的独立配置。

对应的 YAML 配置示例：

```yaml
tutorials4j:
  cache:
    redis:
      time-to-live: 3600000      # 全局默认 1 小时
      cache-null-values: false   # 全局不允许缓存 null
      use-key-prefix: true
      key-prefix: "default"
      named-caches:
        users:
          time-to-live: 7200000   # 2 小时
          cache-null-values: true
          key-prefix: "user"
        verify-code:
          time-to-live: 30000     # 30 秒
          key-prefix: "vc"
```

### 3.2 构建期定制器：NamedCacheManagerBuilderCustomizer

该类实现了 `RedisCacheManagerBuilderCustomizer`，在 `RedisCacheManager.Builder` 创建后被调用，负责填充缓存配置。

```java
@Override
public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
    if (CollectionUtils.isEmpty(options.getNamedCaches())) {
        return;
    }
    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    options.getNamedCaches().forEach((name, redisProp) -> {
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig();
        conf = fillConfiguration(conf, options);      // 先应用全局配置
        conf = fillConfiguration(conf, redisProp);    // 再应用命名缓存特有配置（覆盖）
        configMap.put(name, conf);
    });
    RedisCacheConfiguration defaultConfig = fillConfiguration(RedisCacheConfiguration.defaultCacheConfig(), options);
    builder.cacheDefaults(defaultConfig).withInitialCacheConfigurations(configMap);
}
```

**关键点**：
- **配置继承与覆盖**：每个命名缓存的配置先继承全局 `options`，再被自身专属配置覆盖，符合常规预期。
- **fillConfiguration 方法**：统一处理键前缀、TTL、是否缓存 null 值。注意它调用了 `CacheUtils.cacheNamePrefix()` 和 `CacheUtils.cacheName()`，可能是用于添加固定的应用级前缀，避免多应用共用一个 Redis 实例时的键冲突。

```java
private RedisCacheConfiguration fillConfiguration(RedisCacheConfiguration configuration,
                                                  CacheProperties.Redis prop) {
    configuration = configuration.prefixCacheNameWith(CacheUtils.cacheNamePrefix()); // 全局前缀
    if (prop.getTimeToLive() != null) {
        configuration = configuration.entryTtl(prop.getTimeToLive());
    }
    if (!prop.isCacheNullValues()) {
        configuration = configuration.disableCachingNullValues();
    }
    if (prop.isUseKeyPrefix() && StringUtils.isNotBlank(prop.getKeyPrefix())) {
        configuration = configuration.prefixCacheNameWith(CacheUtils.cacheName(prop.getKeyPrefix()));
    }
    return configuration;
}
```

> 注意：`prefixCacheNameWith` 多次调用会叠加前缀，实际使用时需确保 `CacheUtils.cacheNamePrefix()` 和 `prop.getKeyPrefix()` 不会产生无意义拼接。例如最终键格式可能为 `{appPrefix}:{customPrefix}:cacheName`，需根据设计确认。

### 3.3 运行时初始化定制器：NamedCacheManagerCustomizer

```java
public class NamedCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {
    @Override
    public void customize(RedisCacheManager cacheManager) {
        cacheManager.initializeCaches();
    }
}
```

为什么需要这个类？  
`RedisCacheManager` 实现了 `InitializingBean`，但在 Spring Boot 自动配置的创建流程中，`afterPropertiesSet()` 并不会立即执行，导致通过 `withInitialCacheConfigurations` 注册的命名缓存并未实际创建。调用 `initializeCaches()` 会强制根据已注册的配置预先生成所有 `RedisCache` 实例，确保后续 `@Cacheable` 注解使用时不会因动态创建而覆盖配置。

### 3.4 配置类组装：NamedCacheConfiguration

```java
@Configuration(proxyBeanMethods = false)
public class NamedCacheConfiguration {
    @Bean
    NamedCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(CachesProperties properties) {
        return new NamedCacheManagerBuilderCustomizer(properties.getRedis());
    }
    @Bean
    NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
        return new NamedCacheManagerCustomizer();
    }
}
```

- `@Configuration(proxyBeanMethods = false)` 提升启动性能。
- 两个定制器 Bean 会自动被 Spring Boot 的 `CacheAutoConfiguration` 识别并应用到 `RedisCacheManager` 的创建过程中。

---

## 四、技术亮点

### 4.1 设计亮点

1. **无侵入扩展**：完全基于 Spring Boot 现有的定制器接口，无需重写整个缓存管理器。
2. **配置分层**：全局配置 + 命名缓存配置，支持继承与覆盖，降低重复配置。
3. **预初始化保障**：通过 `CacheManagerCustomizer` 确保命名缓存立即生效，避免运行时降级为默认配置。
4. **日志友好**：在每个关键步骤输出 debug 日志，便于排查缓存配置是否生效。


## 五、使用示例

假设有如下配置：

```yaml
tutorials4j:
  cache:
    redis:
      time-to-live: 60000
      cache-null-values: false
      key-prefix: "global"
      named-caches:
        books:
          time-to-live: 120000
          key-prefix: "bk"
```

在服务中使用：

```java
@Cacheable(value = "books", key = "#id")
public Book getBook(Long id) { ... }
```

- 缓存键实际前缀为 `appPrefix:bk:`（假设 `CacheUtils.cacheNamePrefix()` 返回 `app`）
- TTL 为 120 秒（覆盖全局 60 秒）
- 不会缓存 null 值（继承全局配置）

---

## 六、总结

本文介绍的命名缓存配置方案，通过扩展 `CacheProperties`、定制 `RedisCacheManagerBuilderCustomizer` 和 `CacheManagerCustomizer`，优雅地解决了 Spring Boot Redis 缓存中多区域差异化配置的痛点。该设计遵循框架扩展规范，代码清晰易维护，适合在需要精细控制缓存行为的中大型项目中借鉴使用。

**核心价值**：
- 提供一套可复用的配置模型，降低硬编码；
- 保证配置的可见性与可追溯性；
- 充分发挥 Redis 作为二级缓存的灵活性。

如果你正在为 Spring Boot 应用中的多级缓存配置而烦恼，不妨尝试上述模式，让缓存管理变得更加规范和高效。