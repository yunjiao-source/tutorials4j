#  [008][租户模块]基于Caffeine的租户隔离与两级缓存实践

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在SaaS多租户系统中，缓存隔离是一个必须解决的问题——不同租户的数据不应相互干扰，同时又需要保证高性能。本文以一个轻量级的缓存路由框架为例，分析如何利用 **Caffeine** 实现租户级别的本地缓存隔离，并进一步扩展为 **Caffeine + Redis** 的两级缓存方案。

## 一、核心需求与设计目标

- **租户隔离**：每个租户拥有独立的缓存空间，避免数据窜读。
- **动态路由**：根据当前请求的租户ID，自动选择对应的缓存管理器。
- **两级缓存**：本地Caffeine（一级）提供纳秒级访问，远程Redis（二级）提供跨节点共享，兼顾性能与一致性。
- **延迟初始化**：仅在租户首次访问时创建其专属缓存管理器，避免启动时占用大量内存。

## 二、基础抽象：`AbstractRoutingCacheManager`

该抽象类模仿Spring的`AbstractRoutingDataSource`，作为所有路由缓存管理器的基类。

```java
public abstract class AbstractRoutingCacheManager<T extends CacheManager> implements CacheManager {
    private Map<Object, T> targetCacheManagers = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return determineTargetDataSource().getCache(name);
    }

    protected CacheManager determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        return targetCacheManagers.computeIfAbsent(lookupKey, this::createCacheManager);
    }

    protected abstract Object determineCurrentLookupKey();
    protected abstract T createCacheManager(Object name);
}
```

**设计要点**：
- `determineCurrentLookupKey()`：由子类实现，通常从`TenantContextHolder`中获取当前线程的租户ID。
- `computeIfAbsent`：保证同一租户只创建一个缓存管理器实例，实现按需延迟加载。
- 完全委托模式：所有`CacheManager`接口方法都转发给当前租户对应的管理器。

## 三、租户级Caffeine缓存管理器

```java
public class TenantCaffeineCacheManager extends AbstractRoutingCacheManager<CaffeineCacheManager> {
    private final CaffeineCacheManagerCreator caffeineCacheManagerCreator;

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContextHolder.get();
    }

    @Override
    protected CaffeineCacheManager createCacheManager(Object name) {
        return caffeineCacheManagerCreator.newInstance();
    }
}
```

**亮点**：
- 每个租户获得一个独立的`CaffeineCacheManager`实例，底层Caffeine缓存完全隔离。
- `CaffeineCacheManagerCreator`负责生产默认配置的Caffeine管理器（例如设置过期时间、最大条目等），支持后续定制。

## 四、两级缓存：Caffeine + Redis

多级缓存管理器`MultiLevelCacheManager`（未完整贴出，但从创建器可推断）组合了一级本地缓存和二级分布式缓存。其创建器如下：

```java
public class TenantMultiLevelCacheManagerCreator implements Supplier<MultiLevelCacheManager> {
    private final TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator;
    private final RedisCacheManagerCreator redisCacheManagerCreator;

    @Override
    public MultiLevelCacheManager get() {
        // 双重检查锁单例
        instance = new MultiLevelCacheManager(
            tenantCaffeineCacheManagerCreator.get(),
            redisCacheManagerCreator.get()
        );
        return instance;
    }
}
```

**工作流程推测**：
1. 读操作：先从Caffeine获取，未命中则查Redis，命中后回填Caffeine。
2. 写操作：同时更新Redis并失效Caffeine（或更新Caffeine）。
3. 租户维度的隔离由上下层共同保证：不同的租户使用不同的Caffeine实例，Redis中的key也会拼接租户ID前缀。

## 五、Spring自动配置与创建器模式

`TenantCacheConfiguration`提供`@ConditionalOnMissingBean`的Bean定义，使得使用者可以开箱即用或覆盖默认实现。

```java
@Configuration(proxyBeanMethods = false)
public class TenantCacheConfiguration {
    @Bean
    @ConditionalOnMissingBean
    TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator(CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
        return new TenantCaffeineCacheManagerCreator(caffeineCacheManagerCreator);
    }

    @Bean
    @ConditionalOnMissingBean
    TenantMultiLevelCacheManagerCreator tenantMultiLevelCacheManagerCreator(
            TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator,
            RedisCacheManagerCreator redisCacheManagerCreator) {
        return new TenantMultiLevelCacheManagerCreator(tenantCaffeineCacheManagerCreator, redisCacheManagerCreator);
    }
}
```

**创建器模式的价值**：
- 延迟单例：`TenantCaffeineCacheManagerCreator`保证全局只有一个路由缓存管理器实例，但内部会按需创建租户子管理器。
- 解决循环依赖：通过Supplier接口避免Bean过早初始化，特别适合多级缓存组合场景。

## 六、使用场景与优势

| 场景 | 推荐方案 | 收益 |
|------|----------|------|
| 单租户本地缓存 | `TenantCaffeineCacheManager` | 租户隔离，无脏数据 |
| 多租户共享数据（如配置） | 普通CacheManager | 无需隔离 |
| 高并发读 + 分布式一致性 | 两级缓存（Caffeine+Redis） | 热数据本地命中，冷数据走Redis |
| 租户动态创建/销毁 | 路由机制 + 租户上下文 | 无需重启，自动创建新租户管理器 |

**性能考量**：
- Caffeine是进程内缓存，同一租户的请求可以共享热点数据，大幅降低Redis压力。
- 租户数量较多时，每个租户独立的管理器可能会产生一定内存开销（每个管理器有独立配置和缓存实例）。可通过`maximumSize`限制每租户缓存条目数。

## 七、扩展方向

1. **动态缓存配置**：不同租户可以有不同的Caffeine参数（如过期时间、最大容量）。改造`createCacheManager`方法，根据租户ID从配置中心获取差异化参数。
2. **懒加载与淘汰**：可在租户无活动一段时间后，移除其缓存管理器释放内存。
3. **监控与统计**：暴露每个租户的缓存命中率、大小等指标，便于运维。
4. **多级缓存一致性**：完善写操作时的广播失效机制（例如使用Redis Pub/Sub通知其他节点清理本地Caffeine）。

## 八、总结

这套基于`AbstractRoutingCacheManager`的设计，将“路由”与“具体缓存实现”解耦，实现了优雅的租户隔离。结合Caffeine的高性能和Redis的共享能力，构建了两级缓存方案，非常适合SaaS平台、多租户中间件等场景。开发者只需在请求入口设置`TenantContextHolder`，后续所有缓存操作即可自动路由到正确租户的命名空间，既保证了数据安全，又获得了极致性能。

---

**附：代码文件结构**  
- `AbstractRoutingCacheManager.java`：核心路由基类  
- `TenantCaffeineCacheManager.java`：租户Caffeine实现  
- `TenantMultiLevelCacheManagerCreator.java`：两级缓存创建器  
- `TenantCacheConfiguration.java`：Spring自动配置  

通过这个示例，你可以轻松扩展支持Ehcache、Hazelcast等其他缓存提供者，并实现相同的租户隔离能力。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e