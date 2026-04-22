# cache-redis

## 工具类

### `RedisUtils` – 工具接口

提供静态方法生成统一的`CacheKeyPrefix`：

- `tutorials4jCacheKeyPrefix()`：返回`CacheKeyPrefix.prefixed("tutorials4j:cache:")`
- `tutorials4jCacheKeyPrefix(String name)`：返回`CacheKeyPrefix.prefixed("tutorials4j:cache:" + name + ":")`

另外还提供了一个基于类名+方法名+参数列表的`KeyGenerator`实现，可用于`@Cacheable`注解的`keyGenerator`属性。


## 基于Spring Boot的命名缓存配置Redis设计与实现 


本项目代码在[这里](https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-cache/cache-redis), 示例代码在[这里](https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework/framework-examples/examples-cache-redis) 

### 一、概述

在Spring Boot应用中，`RedisCacheManager`默认提供了一套全局缓存配置（如TTL、缓存空值、Key前缀等）。然而在实际业务中，不同缓存往往需要差异化的策略：例如用户信息缓存希望10分钟过期，而字典数据希望1小时过期。针对这种场景，本文介绍一套“命名缓存”配置方案，允许开发者为每个缓存名称单独定义Redis配置，并在CacheManager启动时自动注册这些缓存实例。

该方案基于Spring Boot的`RedisCacheManagerBuilderCustomizer`和`CacheManagerCustomizer`扩展点实现，核心代码包含5个类：

| 类名 | 职责 |
|------|------|
| `NamedRedisCacheProperties` | 承载外部配置（如`application.yml`），存储每个缓存名称对应的Redis参数 |
| `NamedRedisCacheConfiguration` | Spring配置类，注册两个定制器Bean |
| `NamedRedisCacheManagerBuilderCustomizer` | 实现`RedisCacheManagerBuilderCustomizer`，在Builder构建阶段注入各缓存专属配置 |
| `NamedRedisCacheManagerCustomizer` | 实现`CacheManagerCustomizer`，调用`initializeCaches()`提前初始化所有缓存 |

### 二、核心类功能解析

#### 1. `NamedRedisCacheProperties` – 配置属性绑定

```java
@Data
@ConfigurationProperties(prefix = "tutorials4j.cache")  // 通过PropertiesConsts.PROPERTY_PREFIX_CACHE
public class NamedRedisCacheProperties {
    private Map<String, CacheProperties.Redis> namedRedisCaches = new HashMap<>();
}
```

- 使用`@ConfigurationProperties`绑定以`tutorials4j.cache`为前缀的配置项。
- `namedRedisCaches`是一个Map，Key为缓存名称（如`users`、`products`），Value为`CacheProperties.Redis`类型，内含`timeToLive`、`cacheNullValues`、`keyPrefix`、`useKeyPrefix`等标准属性。
- 示例配置：
  ```yaml
  tutorials4j:
    cache:
      named-redis-caches:
        users:
          time-to-live: 600s
          cache-null-values: false
          key-prefix: "user"
          use-key-prefix: true
        dicts:
          time-to-live: 3600s
          cache-null-values: true
  ```

#### 2. `NamedRedisCacheConfiguration` – 自动配置入口

```java
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NamedRedisCacheProperties.class)
public class NamedRedisCacheConfiguration {
    @Bean
    NamedRedisCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(NamedRedisCacheProperties properties) {
        return new NamedRedisCacheManagerBuilderCustomizer(properties);
    }
    @Bean
    NamedRedisCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
        return new NamedRedisCacheManagerCustomizer();
    }
}
```

- 该配置类在Spring Boot自动配置阶段生效。
- 创建了两个定制器Bean，分别作用于`RedisCacheManager`的构建过程和构建完成后的初始化阶段。

#### 3. `NamedRedisCacheManagerBuilderCustomizer` – 差异化缓存配置注入

核心逻辑：

```java
@Override
public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
    // 1. 构建默认缓存配置：全局Key前缀 "tutorials4j:cache:"
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(RedisUtils.tutorials4jCacheKeyPrefix());

    // 2. 遍历配置的namedRedisCaches，为每个缓存名称生成专属配置
    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    properties.getNamedRedisCaches().forEach((cacheName, redisProp) -> {
        RedisCacheConfiguration config = defaultConfig;
        if (redisProp.getTimeToLive() != null) {
            config = config.entryTtl(redisProp.getTimeToLive());
        }
        if (Boolean.False.equals(redisProp.isCacheNullValues())) {
            config = config.disableCachingNullValues();  // 注意：disable意味着不允许缓存null
        }
        if (Boolean.TRUE.equals(redisProp.isUseKeyPrefix())) {
            String prefix = redisProp.getKeyPrefix();
            config = config.computePrefixWith(RedisUtils.tutorials4jCacheKeyPrefix(prefix));
        }
        configMap.put(cacheName, config);
    });

    // 3. 设置默认配置 + 初始化配置映射
    builder.cacheDefaults(defaultConfig).withInitialCacheConfigurations(configMap);
}
```

**关键点**：
- 默认配置的Key前缀为`tutorials4j:cache:`（由`RedisUtils`定义）。
- 若某缓存配置了`useKeyPrefix: true`且指定了`keyPrefix`（如`user`），则其实际前缀变为`tutorials4j:cache:user:`。
- 最后调用`withInitialCacheConfigurations`注册这些命名缓存，Spring Data Redis会在CacheManager启动时根据这些配置创建对应的`RedisCache`实例。

#### 4. `NamedRedisCacheManagerCustomizer` – 强制初始化缓存

```java
public class NamedRedisCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {
    @Override
    public void customize(RedisCacheManager cacheManager) {
        cacheManager.initializeCaches();   // 立即初始化所有已注册的缓存
    }
}
```

- `RedisCacheManager`默认是懒加载模式：当首次调用`getCache(name)`时才真正创建缓存实例。但在命名缓存场景下，我们希望应用启动后所有定义的缓存就已准备就绪，便于监控和一致性。
- 通过`initializeCaches()`方法，会遍历`withInitialCacheConfigurations`中注册的所有缓存名称并创建对应的`RedisCache`对象。


### 三、工作流程总览

1. **配置加载**：Spring Boot启动时，`@EnableConfigurationProperties`将`application.yml`中的`tutorials4j.cache.named-redis-caches`映射到`NamedRedisCacheProperties`对象。
2. **定制器注册**：`NamedRedisCacheConfiguration`向容器注册两个定制器Bean。
3. **构建RedisCacheManager**：Spring Boot的`RedisCacheConfiguration`自动配置类创建`RedisCacheManagerBuilder`，然后调用所有`RedisCacheManagerBuilderCustomizer`。`NamedRedisCacheManagerBuilderCustomizer`会为每个命名缓存生成专属`RedisCacheConfiguration`并传入builder。
4. **后处理初始化**：`RedisCacheManager`实例创建后，所有`CacheManagerCustomizer<RedisCacheManager>`被执行。`NamedRedisCacheManagerCustomizer`调用`initializeCaches()`，立即创建所有命名缓存实例。
5. **运行时使用**：在Service层使用`@Cacheable(cacheNames = "users", key = "#id")`时，Spring会自动从`RedisCacheManager`获取名为`users`的缓存，并使用预先配置的TTL和Key前缀。

### 四、使用示例

#### 步骤1：添加依赖（假设已集成Spring Data Redis）

#### 步骤2：编写配置文件 `application.yml`

```yaml
tutorials4j:
  cache:
    named-redis-caches:
      users:
        time-to-live: 5m
        cache-null-values: false
        key-prefix: "user"
        use-key-prefix: true
      roles:
        time-to-live: 1h
        cache-null-values: true   # 允许缓存null
        use-key-prefix: false     # 使用全局前缀
```

#### 步骤3：启用缓存注解

```java
@Service
public class UserService {
    @Cacheable(cacheNames = "users", key = "#id")
    public User getUser(Long id) {
        // 数据库查询逻辑
    }
}
```

#### 步骤4：验证Key格式

- `users`缓存的Key前缀为`tutorials4j:cache:user:`，最终Redis Key为`tutorials4j:cache:user::<id值>`
- `roles`缓存使用全局前缀`tutorials4j:cache:`，Key为`tutorials4j:cache::<key>`

### 五、注意事项

3. **支持动态注册缓存**  
   当前方案仅支持启动时通过配置定义的缓存名称。若业务中需要动态创建新缓存，也支持动态创建，只是使用默认配置。

4. **性能影响**  
   `initializeCaches()`会立即创建所有缓存实例，若命名缓存数量非常多（例如上百个），会增加启动时间。

### 六、总结

本文介绍的命名Redis缓存配置方案，通过Spring Boot的扩展机制实现了：
- **差异化TTL、空值策略、Key前缀**：每个缓存独立配置。
- **声明式配置**：在`application.yml`中集中管理，无需修改代码。
- **启动时预热**：所有缓存提前初始化，避免首次访问时的延迟创建。

该方案适用于多业务模块共用同一个RedisCacheManager，且各模块对缓存策略要求不同的场景。读者可根据实际需求调整`cacheNullValues`的处理逻辑以及前缀分隔符规范。