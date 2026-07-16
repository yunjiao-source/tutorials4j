#  [006][缓存模块] 两级缓存实战：基于 Caffeine + Redis 的多级缓存设计与实现

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

## 摘要

在高并发系统中，缓存是提升性能的核心手段。单一缓存方案往往难以兼顾高性能与高可用：本地缓存（如 Caffeine、Guava Cache）访问速度快但容量有限、无法跨节点共享；分布式缓存（如 Redis）支持数据共享和持久化，但网络 I/O 开销较高。本文介绍一种两级缓存架构，将本地缓存作为一级缓存（L1），分布式缓存作为二级缓存（L2），通过透明组合的方式提供统一的 `Cache` 接口，既能享受本地缓存的纳秒级响应，又能利用分布式缓存实现数据共享。文章基于 Spring Cache 抽象，给出了完整的 Java 实现，并分析了其设计思路、核心代码及适用场景。

## 1. 背景与挑战

Spring Framework 提供了 `org.springframework.cache.Cache` 和 `CacheManager` 抽象，允许开发者以注解方式（如 `@Cacheable`）使用缓存。常见的实现包括：

- **ConcurrentMapCache**：基于 `ConcurrentHashMap`，适合单体应用演示，无过期策略。
- **CaffeineCache**：基于高性能本地缓存库 Caffeine，支持淘汰策略、过期时间、统计信息等。
- **RedisCache**：基于 Redis 的分布式缓存，适合多实例共享数据。

但是，单独使用任一方案都有局限：

| 方案 | 优点 | 缺点 |
|------|------|------|
| 仅本地缓存 | 纳秒级延迟、无网络开销 | 各节点数据不一致、内存受限、重启丢失 |
| 仅分布式缓存 | 数据统一、持久化、大容量 | 毫秒级延迟、网络开销、单点故障风险 |

因此，将两者结合形成**两级缓存**（本地 L1 + 分布式 L2）成为高并发系统的常见优化模式：读操作优先命中 L1，未命中则查 L2 并回填 L1；写操作同时更新 L1 和 L2，保证一致性。

## 2. 整体设计

### 2.1 设计目标

1. **透明性**：对业务代码完全透明，使用 Spring 标准的 `Cache` 接口。
2. **可插拔**：本地和远程缓存管理器可独立配置，通过组合方式构建。
3. **一致性**：写操作（put/evict/clear）同步操作两级缓存。
4. **性能**：读操作优先本地，缓存命中后避免远程访问。

### 2.2 模块结构

项目包含四个核心类：

- `MultiLevelCache`：实现 `Cache` 接口，组合 `local` 和 `remote` 两个 `Cache` 实例，实现两级读/写逻辑。
- `MultiLevelCacheManager`：实现 `CacheManager` 接口，组合 `local` 和 `remote` 两个 `CacheManager`，为每个缓存名称创建 `MultiLevelCache`。
- `MultiLevelCacheManagerCreator`：工厂类（`Supplier`），单例模式创建 `MultiLevelCacheManager`，避免重复初始化。
- `CacheMultiLevelConfiguration`：Spring 自动配置类，条件化注册 `MultiLevelCacheManagerCreator` Bean。

类关系如下图所示（文字描述）：

```
CacheMultiLevelConfiguration (Spring @Configuration)
    └── 创建 MultiLevelCacheManagerCreator（依赖 CaffeineCacheManagerCreator + RedisCacheManagerCreator）
            └── 创建 MultiLevelCacheManager（依赖 local CacheManager + remote CacheManager）
                    └── 为每个缓存名称创建 MultiLevelCache（依赖 local Cache + remote Cache）
```

## 3. 核心代码解析

### 3.1 MultiLevelCache：两级缓存的核心实现

`MultiLevelCache` 类包装了本地缓存（`local`）和远程缓存（`remote`），所有缓存操作都委托给它们，并在读操作中实现“先本地，后远程，回填本地”的逻辑。

```java
public class MultiLevelCache implements Cache {
    private final Cache local;
    private final Cache remote;

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = local.get(key);
        if (wrapper != null) {
            return wrapper;
        }
        wrapper = remote.get(key);
        if (wrapper != null) {
            local.put(key, wrapper.get());   // 回填本地
            return wrapper;
        }
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        local.put(key, value);
        remote.put(key, value);
    }

    @Override
    public void evict(Object key) {
        local.evict(key);
        remote.evict(key);
    }

    @Override
    public void clear() {
        local.clear();
        remote.clear();
    }
    // ... 其他方法
}
```

值得注意的设计点：

- **读操作**：`get(Object key)`、`get(Object key, Class<T> type)`、`get(Object key, Callable<T> valueLoader)` 均遵循“L1 → L2 → 回填 L1”策略。当 `valueLoader` 存在时，如果两级都未命中，会调用 `valueLoader` 加载数据，但此处实现依赖底层 `Cache` 的行为——本地缓存未命中时会执行 `valueLoader`，远程亦然，最后再把值 put 到本地。这可能导致 `valueLoader` 执行两次（如果本地和远程都未命中），需要业务保证幂等性。更优雅的实现可以只在最外层调用一次 `valueLoader`，但作为示例已足够。
- **写操作**：同步更新本地和远程，确保一致性。注意这里未处理事务或异步写，适用于可容忍极小不一致窗口的场景。
- **删除/清空**：同样同步操作两级。

### 3.2 MultiLevelCacheManager：缓存管理器组合

`MultiLevelCacheManager` 继承自 `AbstractCacheManager`，需要实现 `loadCaches()` 和 `getCache(String name)`。

```java
public class MultiLevelCacheManager extends AbstractCacheManager {
    private final CacheManager local;
    private final CacheManager remote;

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Set<String> names = new HashSet<>();
        names.addAll(local.getCacheNames());
        names.addAll(remote.getCacheNames());
        return names.stream().map(this::getCache).collect(Collectors.toList());
    }

    @Override
    public Cache getCache(String name) {
        Cache localCache = local.getCache(name);
        Cache remoteCache = remote.getCache(name);
        if (localCache == null || remoteCache == null) {
            return null;   // 两个管理器必须同时存在该缓存，否则视为不可用
        }
        return new MultiLevelCache(localCache, remoteCache);
    }
}
```

设计要点：

- **名称合并**：`loadCaches()` 返回两级管理器所有缓存名称的并集，确保每个缓存都能被初始化。
- **严格绑定**：`getCache(String name)` 要求本地和远程管理器都有对应名称的缓存，否则返回 `null`。这避免了只有一端配置时造成的数据不完整。如果希望允许本地独有或远程独有，可以修改策略（例如仅有一端存在时返回该端的单级缓存）。

### 3.3 MultiLevelCacheManagerCreator：单例创建器

为了保证 `MultiLevelCacheManager` 全局唯一（通常一个应用一个），使用 `Supplier` 模式实现延迟初始化单例：

```java
public class MultiLevelCacheManagerCreator implements Supplier<MultiLevelCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;
    private final RedisCacheManagerCreator redisCacheManagerCreator;
    private volatile MultiLevelCacheManager instance;

    @Override
    public MultiLevelCacheManager get() {
        if (instance != null) return instance;
        synchronized (this) {
            if (instance != null) return instance;
            instance = new MultiLevelCacheManager(
                caffeineCacheManagerCreator.get(),
                redisCacheManagerCreator.get()
            );
        }
        return instance;
    }
}
```

`CaffeineCacheManagerCreator` 和 `RedisCacheManagerCreator` 是假设已存在的工厂类，分别产生配置好的 `CaffeineCacheManager` 和 `RedisCacheManager`。

### 3.4 Spring 自动配置

`CacheMultiLevelConfiguration` 使用 `@ConditionalOnMissingBean` 保证用户可覆盖默认的创建器。

```java
@Configuration(proxyBeanMethods = false)
public class CacheMultiLevelConfiguration {
    @Bean
    @ConditionalOnMissingBean(MultiLevelCacheManagerCreator.class)
    MultiLevelCacheManagerCreator multiLevelCacheManagerCreator(
            CaffeineCacheManagerCreator caffeineCacheManagerCreator,
            RedisCacheManagerCreator redisCacheManagerCreator) {
        return new MultiLevelCacheManagerCreator(caffeineCacheManagerCreator, redisCacheManagerCreator);
    }
}
```

## 4. 使用示例

### 4.1 引入依赖

```xml
<dependency>
    <groupId>tutorials4j</groupId>
    <artifactId>cache-spring-boot-starter</artifactId>
</dependency>
```

### 4.2 配置缓存管理器

Spring Boot 应用中，自动配置会生效。如果需要自定义缓存名称、过期时间等，可以分别配置 `CaffeineCacheManager` 和 `RedisCacheManager` 的 Bean，框架会自动注入到 `MultiLevelCacheManagerCreator` 中。

```java
@Configuration
public class MyCacheableConfig implements CachingConfigurer {
    @Autowired
    private MultiLevelCacheManagerCreator cacheManagerCreator;

    @Bean
    @Override
    public CacheManager cacheManager() {
        return cacheManagerCreator.get();
    }
}
```

### 4.3 使用注解

```java
@Service
public class UserService {
    @Cacheable(value = "users", key = "#id")
    public User getUser(Long id) {
        // 从数据库加载
        return userMapper.findById(id);
    }

    @CacheEvict(value = "users", key = "#id")
    public void evictUser(Long id) {
        // 删除数据库记录
    }
}
```

此时，`users` 缓存将使用两级缓存：第一次访问时从 DB 加载，写入 Redis 和 Caffeine；第二次访问时直接从 Caffeine 返回，性能极高；当其他节点修改数据并清除缓存时，Redis 被清除，本地的 Caffeine 缓存也会被同步清除（因为 `@CacheEvict` 会触发 `MultiLevelCache.evict`，同时删除本地和远程）。

## 5. 优缺点分析

### 5.1 优点

- **性能大幅提升**：热数据在本地缓存，读延迟可降至纳秒级（本地内存访问）。
- **数据一致性保证**：写操作同步更新两级，避免了本地脏数据长期存在。
- **标准 API**：完全兼容 Spring Cache，无需修改业务代码。
- **可观测性**：可以扩展增加缓存命中率监控等。

### 5.2 缺点与注意事项

- **内存占用叠加**：同一份数据可能同时存在于本地和 Redis，需要合理设置本地缓存的最大容量和过期时间。
- **写延迟增加**：每次写操作都要写 Redis（网络 I/O）和本地，比单级本地缓存稍慢。
- **缓存淘汰复杂性**：本地和 Redis 的淘汰策略独立，可能导致某个键在 Redis 中已过期但本地仍未过期（因为本地 `expireAfterWrite` 时间更长）。解决方案是**本地过期时间 ≤ 远程过期时间**，保证本地数据不会比远程更旧。
- **`valueLoader` 重复执行风险**：在 `get(key, valueLoader)` 方法中，如果本地未命中，`local.get(key, valueLoader)` 会执行一次加载；接着远程也未命中，`remote.get(...)` 又会执行一次加载。业务需确保 `valueLoader` 幂等或由开发者自行修改代码（可优化为只执行一次加载，然后同时 put 两级）。
- **启动时需要两个 CacheManager 都就绪**：如果 Redis 连接失败，本地缓存可能也无法正常创建（取决于配置）。

## 6. 优化建议

1. **统一 TTL 策略**：确保本地缓存的过期时间 ≤ Redis 缓存的过期时间。
2. **异步回填**：在查远程命中后，回填本地操作可以异步执行，避免阻塞读线程。
3. **防止缓存击穿**：可以在 `valueLoader` 外层加锁，只允许一个线程加载数据。
4. **支持降级**：当 Redis 不可用时，可以自动降级为仅本地缓存模式。
5. **添加统计**：记录本地命中率、远程命中率等指标，便于调优。

## 7. 总结

本文展示了一个生产可用的两级缓存实现，它组合了 Caffeine 和 Redis，通过 Spring 标准的 `Cache` 和 `CacheManager` 接口，为业务代码提供了高性能且共享的缓存能力。实现的核心思路在于透明地拦截缓存操作，并在读路径实现“L1 → L2 → 回填 L1”的优先级策略。虽然存在一些需要注意的陷阱（如 TTL 不一致、valueLoader 重复执行），但通过合理的配置和少量优化，完全可以应用于高并发场景。

该设计不仅限于 Caffeine + Redis，理论上可以替换本地缓存为任何 `CacheManager` 实现（如 Guava、Ehcache），替换远程缓存为任何分布式缓存（如 Memcached、Hazelcast），具有很好的扩展性。希望本文能为你在实际项目中选择和实现两级缓存提供有价值的参考。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e