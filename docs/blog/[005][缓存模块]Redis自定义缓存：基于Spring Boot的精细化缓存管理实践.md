# [005][缓存模块]Redis自定义缓存：基于Spring Boot的精细化缓存管理实践

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在分布式系统中，缓存是提升性能的关键手段。Redis 作为高性能的键值存储，常与 Spring Cache 抽象结合使用。Spring Boot 提供了自动配置的 `RedisCacheManager`，但在实际业务中，我们往往需要为不同缓存设置独立的 TTL、前缀、是否允许空值等个性化配置。本文介绍一套精细化、可扩展的 Redis 缓存自定义方案，通过定制器模式和显式初始化，实现对缓存的全面管控。

## 一、为什么需要自定义 Redis 缓存？

Spring Boot 的 `RedisCacheManager` 默认使用统一的 `RedisCacheConfiguration`，所有缓存共享相同的 TTL、键前缀等。这无法满足多业务场景下的差异化需求。例如：
- 用户会话缓存希望 30 分钟过期
- 商品详情缓存希望 1 小时过期
- 字典数据希望永不过期（或很长）

除了配置差异化，还存在一个隐性问题：`RedisCacheManager` 虽然实现了 `InitializingBean`，但在某些自动配置场景下，`afterPropertiesSet` 并不会自动执行，导致预定义的命名缓存配置无法被加载，运行时获取缓存会退化为默认配置。因此需要手动触发初始化。

## 二、整体设计思路

本方案基于 Spring Boot 的扩展机制，提供以下核心组件：

| 组件 | 职责 |
|------|------|
| `CacheRedisProperties` | 承载全局和每个命名缓存的配置（TTL、KeyPrefix、cacheNullValues 等） |
| `NamedRedisCacheManagerBuilderCustomizer` | 在构建 `RedisCacheManager` 时，将命名缓存配置转换为独立的 `RedisCacheConfiguration` |
| `NamedCacheManagerCustomizer` | 在 `RedisCacheManager` 创建后，强制调用 `initializeCaches()`，确保预置缓存生效 |
| `RedisUtils` | 工具类：生成带租户前缀的 `CacheKeyPrefix`、合并配置 |
| `RedisCacheManagerCreator` | 集中管理 `RedisCacheManager` 的创建过程，整合所有定制器 |
| `CacheRedisConfiguration` | 自动配置类，按条件注册上述 Bean |

整体流程如下图所示（文字描述）：
1. 应用启动，加载 `CacheRedisProperties` 配置。
2. `CacheRedisConfiguration` 注册 `NamedRedisCacheManagerBuilderCustomizer` 及其他 Bean。
3. `RedisCacheManagerCreator` 获取 `RedisConnectionFactory`，构建 `RedisCacheManagerBuilder`。
4. 定制器遍历命名缓存，为每个缓存生成独立的 `RedisCacheConfiguration`，并调用 `builder.withInitialCacheConfigurations()` 预置。
5. 构建 `RedisCacheManager` 实例后，再通过 `NamedCacheManagerCustomizer` 调用 `initializeCaches()`，确保配置立即生效。

## 三、核心组件详解

### 3.1 配置属性类（CacheRedisProperties）

为了方便配置，定义 `CacheRedisProperties` 和 `RedisOptions`

```yaml
tutorials4j:
  cache:
    redis:
      enable-statistics: true
      time-to-live: 3600s          # 全局默认TTL
      cache-null-values: false     # 全局是否缓存null
      use-key-prefix: true
      key-prefix: ""               # 全局前缀（实际还会叠加租户）
      named-caches:
        users:
          time-to-live: 1800s
          cache-null-values: true
          key-prefix: "user"
        products:
          time-to-live: 7200s
```

### 3.2 RedisUtils – 键前缀与配置填充

`RedisUtils` 提供两个核心功能：
- **多租户前缀**：`defaultCacheKeyPrefix()` 结合 `TenantContextHolder`，自动生成包含租户标识的完整前缀，例如 `tenantA:users::`。
- **配置合并**：`fillConfiguration()` 将 `RedisOptions` 中的 TTL、空值允许、自定义前缀等属性应用到 `RedisCacheConfiguration`。

关键代码片段：

```java
static CacheKeyPrefix defaultCacheKeyPrefix() {
    return name -> TenantContextHolder.get() + ":" + name + "::";
}

static RedisCacheConfiguration fillConfiguration(RedisCacheConfiguration configuration,
                                                  RedisOptions prop) {
    if (prop.getTimeToLive() != null) {
        configuration = configuration.entryTtl(prop.getTimeToLive());
    }
    if (!prop.isCacheNullValues()) {
        configuration = configuration.disableCachingNullValues();
    }
    if (prop.isUseKeyPrefix() && StringUtils.isNotBlank(prop.getKeyPrefix())) {
        configuration = configuration.computePrefixWith(defaultCacheKeyPrefix(prop.getKeyPrefix()));
    }
    return configuration;
}
```

### 3.3 NamedRedisCacheManagerBuilderCustomizer – 构建期定制

该类实现 `RedisCacheManagerBuilderCustomizer`，在 `RedisCacheManagerBuilder` 构建阶段介入。它从 `properties` 中读取 `namedCaches` 映射，为每个缓存名创建独立的 `RedisCacheConfiguration`（基于全局默认配置覆盖个性化参数），最后调用 `builder.withInitialCacheConfigurations(configMap)` 预置。

```java
@Override
public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
    if (CollectionUtils.isEmpty(properties.getNamedCaches())) {
        return;
    }
    RedisCacheConfiguration defaultConfig = builder.cacheDefaults();
    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    properties.getNamedCaches().forEach((name, options) -> {
        RedisCacheConfiguration customConfig = RedisUtils.fillConfiguration(defaultConfig, options);
        configMap.put(name, customConfig);
    });
    builder.withInitialCacheConfigurations(configMap);
}
```

### 3.4 NamedCacheManagerCustomizer – 初始化触发器

`CacheManagerCustomizer<RedisCacheManager>` 会在 `RedisCacheManager` 实例创建后被调用。它仅做一件事：`cacheManager.initializeCaches()`。这确保所有通过 `withInitialCacheConfigurations` 预定义的缓存被立即初始化，而不是等到第一次访问时才懒加载，避免配置丢失。

```java
@Override
public void customize(RedisCacheManager cacheManager) {
    cacheManager.initializeCaches();
}
```

### 3.5 RedisCacheManagerCreator – 聚合创建逻辑

为了避免分散的 Bean 定义导致创建顺序混乱，`RedisCacheManagerCreator` 作为 `Supplier<RedisCacheManager>` 集中处理：
1. 从 `RedisConnectionFactory` 创建 Builder。
2. 设置全局默认配置（来自 `properties`）。
3. 应用所有 `RedisCacheManagerBuilderCustomizer`（包括自定义的命名缓存定制器）。
4. 构建 `RedisCacheManager`。
5. 应用所有 `CacheManagerCustomizer<RedisCacheManager>`（包括初始化触发器）。
6. 返回单例实例。

该组件被注册为 Spring Bean，需要用到 `RedisCacheManager` 的地方可注入此 `Supplier` 或直接获取其生成的实例。

### 3.6 自动配置类 CacheRedisConfiguration

自动配置类使用 `@ConditionalOnMissingBean` 保证用户可覆盖默认实现。它注册了三个核心 Bean：
- `NamedRedisCacheManagerBuilderCustomizer`
- `NamedCacheManagerCustomizer`
- `RedisCacheManagerCreator`

同时，通过 `ObjectProvider` 收集所有实现 `RedisCacheManagerBuilderCustomizer` 和 `CacheManagerCustomizer` 的 Bean，传递给 Creator，形成可扩展的定制链。

## 四、如何使用这套自定义方案

### 步骤1：引入依赖与配置

在 `application.yml` 中添加 Redis 连接信息及缓存配置：

```yaml
spring:
  redis:
    host: localhost
    port: 6379

tutorials4j:
  cache:
    redis:
      enable-statistics: true
      time-to-live: 3600s
      cache-null-values: false
      named-caches:
        userCache:
          time-to-live: 1800s
          cache-null-values: true
          key-prefix: "user"
        productCache:
          time-to-live: 7200s
```

### 步骤2：启用缓存并注入 RedisCacheManager

确保主类或配置类上标注 `@EnableCaching`。然后可以注入 `RedisCacheManager`：

```java
@Autowired
private RedisCacheManager cacheManager;
```

或者使用 `RedisCacheManagerCreator` 手动控制创建时机。

### 步骤3：使用 @Cacheable 等注解

```java
@Cacheable(value = "userCache", key = "#userId")
public User getUser(Long userId) { ... }
```

此时 `userCache` 将使用独立的 1800s TTL、允许缓存 null，且键前缀为 `{租户}:user:userCache::`。

## 五、核心要点总结

1. **差异化配置**：通过 `named-caches` 结合 `RedisCacheManagerBuilderCustomizer`，为不同缓存注入独立配置。
2. **强制初始化**：通过 `CacheManagerCustomizer` 调用 `initializeCaches()`，确保预置缓存配置立即生效，避免懒加载导致配置不一致。
3. **租户隔离**：`RedisUtils` 中的 `defaultCacheKeyPrefix` 自动从 `TenantContextHolder` 获取租户标识，实现多租户数据天然隔离。
4. **可扩展性**：提供 `@ConditionalOnMissingBean`，用户可覆盖或新增定制器，无需修改框架代码。
5. **生命周期整合**：`RedisCacheManagerCreator` 作为单一入口，保证定制器的执行顺序（先 BuilderCustomizer，后 CacheManagerCustomizer）。

## 六、常见问题

- **为什么不能只靠 `withInitialCacheConfigurations`？**  
  该方法只是声明了初始配置，实际 Cache 对象仍然是在第一次访问时才创建。`initializeCaches()` 强制提前创建，确保后续使用直接命中预置配置。

- **是否支持动态增减缓存？**  
  可以的，运行时新增缓存使用默认属性配置。

- **与 Spring Boot 默认 `RedisCacheManager` 的关系**  
  本方案完全兼容，只是通过定制器增强了默认行为。若不使用本配置，Spring Boot 仍会按原有方式工作。

## 七、结语

通过组合 `RedisCacheManagerBuilderCustomizer` 与 `CacheManagerCustomizer`，我们实现对 Redis 缓存管理器的精细控制，既满足了多业务场景的差异化配置需求，又解决了预置缓存初始化的问题。该方案已在实际项目中稳定运行，代码结构清晰，易于扩展。希望本文能帮助读者更好地理解和定制 Spring Cache + Redis 的缓存层。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e