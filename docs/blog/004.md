# [004][缓存模块]Caffeine缓存自定义：构建灵活的Spring Boot缓存管理器

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在Spring Boot应用中，缓存是提升系统性能的重要手段。Caffeine作为高性能的本地缓存库，常与Spring的`CacheManager`抽象整合使用。然而，默认的`CaffeineCacheManager`仅支持全局统一的缓存配置（如过期时间、最大容量等），无法针对不同业务缓存进行差异化定制。本文将通过分析一个自定义的Caffeine缓存模块，展示如何实现支持“每个缓存独立配置”的灵活缓存管理器。

## 一、背景与目标

### 1.1 痛点
- 业务缓存需求各异：用户会话缓存需要较短过期时间，商品目录缓存可设置较长过期时间且需要自动刷新。
- 默认`CaffeineCacheManager`的配置是全局的，若想差异化，只能创建多个`CacheManager`实例，管理复杂。

### 1.2 设计目标
- 支持全局默认配置 + 每个缓存名称可覆盖配置。
- 复用Spring的`CaffeineCacheManager`，尽量减少侵入。
- 提供线程安全的缓存管理器创建器，支持延迟初始化。
- 保持与Spring Boot自动配置的无缝集成。

## 二、模块功能解析

项目提供了四个核心类，分别负责配置加载、工具方法、缓存管理器创建和个性化缓存生成。

### 2.1 `CacheCaffeineConfiguration` – Spring配置装配

```java
@Configuration(proxyBeanMethods = false)
public class CacheCaffeineConfiguration {
    @Bean
    Caffeine<Object, Object> caffeine(CacheCaffeineProperties properties) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        CaffeineUtils.copyOption(caffeine, properties);
        return caffeine;
    }

    @Bean
    @ConditionalOnMissingBean(CaffeineCacheManagerCreator.class)
    CaffeineCacheManagerCreator caffeineCacheManagerCreator(Caffeine<Object, Object> caffeine,
                                              CacheCaffeineProperties properties) {
        return new CaffeineCacheManagerCreator(properties, caffeine);
    }
}
```

- **作用**：声明创建`Caffeine`实例的Bean，并通过`CaffeineUtils`将全局属性（如`initialCapacity`、`maximumSize`等）复制到构建器中。然后创建`CaffeineCacheManagerCreator`，它作为工厂负责生成最终的`CacheManager`。
- **亮点**：使用`@ConditionalOnMissingBean`允许用户覆盖默认的创建器实现，提供扩展点。

### 2.2 `CaffeineUtils` – 配置复制工具

```java
public interface CaffeineUtils {
    static void copyOption(Caffeine<Object, Object> caffeine, CaffeineOptions options) {
        caffeine.initialCapacity(options.getInitialCapacity())
                .maximumSize(options.getMaximumSize());
        if (options.getExpireAfterAccess() != null) {
            caffeine.expireAfterAccess(options.getExpireAfterAccess());
        }
        // ... 其他可选配置
        if (Objects.equals(Boolean.TRUE, options.getRecordStats())) {
            caffeine.recordStats();
        }
    }
}
```

- **作用**：将`CaffeineOptions`（包含过期策略、刷新间隔、统计开关等）批量设置到`Caffeine`构建器中。
- **设计**：声明为接口静态方法，便于复用；判空处理支持可选配置。

### 2.3 `CaffeineCacheManagerCreator` – 线程安全的单例创建器

```java
public class CaffeineCacheManagerCreator implements Supplier<CaffeineCacheManager> {
    private final CacheCaffeineProperties properties;
    private final Caffeine<Object, Object> caffeine;
    private volatile CaffeineCacheManager instance;

    @Override
    public CaffeineCacheManager get() {
        if (instance != null) return instance;
        synchronized (this) {
            if (instance != null) return instance;
            instance = newInstance();
        }
        return instance;
    }

    public CaffeineCacheManager newInstance() {
        FlexibleCaffeineCacheManager manager = new FlexibleCaffeineCacheManager(properties);
        manager.setCaffeine(caffeine);
        return manager;
    }
}
```

- **作用**：实现`Supplier`接口，提供单例的`CaffeineCacheManager`。使用DCL（双重检查锁）保证线程安全，同时避免每次获取都创建新实例。
- **优势**：延迟加载，仅在首次调用`get()`时才真正构建缓存管理器；可作为Spring Bean直接注入，也可在需要时手动获取。

### 2.4 `FlexibleCaffeineCacheManager` – 核心个性化逻辑

```java
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager {
    private final CacheCaffeineProperties properties;

    @Override
    protected Cache<Object, Object> createNativeCaffeineCache(String name) {
        Map<String, CaffeineOptions> optionsMap = properties.getNamedCaches();
        if (MapUtils.isNotEmpty(optionsMap) && optionsMap.containsKey(name)) {
            CaffeineOptions caffeineOptions = optionsMap.get(name);
            caffeineOptions.mergeNullValue(properties); // 未设置的项回填全局值
            Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
            CaffeineUtils.copyOption(caffeine, caffeineOptions);
            log.debug("初始化缓存: {}", name);
            return caffeine.build();
        }
        return super.createNativeCaffeineCache(name);
    }
}
```

- **关键重写**：`createNativeCaffeineCache(String name)`是`CaffeineCacheManager`中每个缓存创建时调用的方法。我们覆盖它，根据缓存名称从`properties.getNamedCaches()`中查找专属配置。
- **配置合并**：`caffeineOptions.mergeNullValue(properties)`将未显式设置的属性（如`expireAfterWrite`为null）继承自全局配置，避免遗漏。
- **降级处理**：若没有专属配置，则调用父类方法，父类会使用默认的`Caffeine`实例（即全局配置）来构建缓存。

## 三、自定义扩展的设计精髓

### 3.1 属性模型设计

假设`CacheCaffeineProperties`包含：
- 全局配置：`initialCapacity`、`maximumSize`、`expireAfterWrite`等。
- `Map<String, CaffeineOptions> namedCaches`：key为缓存名称，value为该缓存的个性化配置。

`CaffeineOptions`中定义与全局配置相同的字段，并提供一个`mergeNullValue`方法：对于自身为null的属性，从传入的默认配置中取值。

### 3.2 为什么需要`CaffeineCacheManagerCreator`？

直接暴露`FlexibleCaffeineCacheManager`作为Bean也可以，但创建器的好处：
1. **单例控制**：确保整个应用只有一个`CacheManager`实例（Spring默认也如此，但通过Supplier可更明确控制创建时机）。
2. **延迟初始化**：避免在Spring容器启动阶段过早构建缓存（尤其是在缓存配置依赖外部动态参数时）。
3. **测试友好**：可mock或替换创建逻辑。

### 3.3 线程安全与性能

`CaffeineCacheManager`本身是线程安全的，但`createNativeCaffeineCache`会被多次调用（每个缓存名称调用一次）。`FlexibleCaffeineCacheManager`中每次有专属配置时都会新建一个`Caffeine`实例，这符合预期——不同缓存本就应拥有独立的配置和缓存实例。而`CaffeineCacheManagerCreator`中的DCL确保全局只有一个管理实例，避免了重复创建管理器的开销。

## 四、使用示例与配置

### 4.1 配置文件（application.yml）

```yaml
tutorials4j:
  cache:
    caffeine:
      allow-null-values: false
      initial-capacity: 16
      maximum-size: 1000
      expire-after-write: 60s
      record-stats: true
      named-caches:
        users:
          expire-after-write: 30s
          maximum-size: 500
        products:
          expire-after-access: 10m
          refresh-after-write: 5m
```

- 全局：默认写入后60秒过期，最大1000条。
- `users`缓存：写入后30秒过期，最大500条（覆盖全局过期和最大容量）。
- `products`缓存：访问后10分钟过期，写入后5分钟刷新（注意`refreshAfterWrite`需要搭配`CacheLoader`，此处仅为示例）。

### 4.2 启用缓存

```java
@Configuration
@EnableCaching
public class MyCacheConfig {
    @Bean
    public CacheManager cacheManager(CaffeineCacheManagerCreator creator) {
        return creator.get(); // 获取单例缓存管理器
    }
}
```

或者在自动配置中直接使用`CacheCaffeineConfiguration`，它会自动装配创建器并暴露`CacheManager`。

### 4.3 业务使用

```java
@Service
public class ProductService {
    @Cacheable("products")
    public Product getProduct(Long id) { ... }

    @CacheEvict(cacheNames = "users", key = "#userId")
    public void evictUser(String userId) { ... }
}
```

此时`products`缓存将应用5分钟刷新+10分钟访问过期策略，而`users`缓存则使用30秒写入过期。

## 五、总结

通过上述设计，我们实现了：
- **细粒度配置**：每个缓存名称可定义独立的过期、容量等参数。
- **线程安全且高效**：单例管理器 + DCL，避免重复构造。
- **易扩展**：通过`@ConditionalOnMissingBean`允许用户替换创建器或缓存管理器。
- **兼容Spring标准**：继承`CaffeineCacheManager`，所有Spring缓存注解（`@Cacheable`等）无需改动。


Caffeine作为“现代Java本地缓存之王”，配合灵活的缓存管理器，能让你的应用在性能和业务需求之间取得完美平衡。希望本文的自定义实现能为你的项目提供有益的参考。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e