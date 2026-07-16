# [064][缓存模块]两级缓存实战：基于 Caffeine 和 Redis 的多级缓存设计与实现

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在微服务和分布式系统中，缓存是提升性能、降低数据库压力的核心手段。然而，单一缓存方案往往难以两全：**本地缓存**（如 Caffeine、Guava）虽然访问速度极快（纳秒级），但无法在多个服务实例间共享，且受限于堆内存；**远程缓存**（如 Redis）虽然支持跨节点共享、容量可扩展，但网络 I/O 和序列化开销带来了数十毫秒的延迟。**两级缓存（多级缓存）** 应运而生——将本地缓存作为一级（L1），远程缓存作为二级（L2），兼顾速度与共享，既降低热点数据访问延迟，又减少对后端存储的压力。

本文以一套生产级 Java 缓存框架（基于 Spring Cache 抽象）的源码为蓝本，深入剖析两级缓存的设计思路、核心策略、并发控制以及灵活的配置体系。代码整合了 Caffeine（L1）和 Redis（L2），并提供了友好的扩展点，适用于高并发读多写少场景。


## 一、整体架构

### 1.1 类层次结构

该框架围绕 Spring 的 `CacheManager` 和 `Cache` 接口展开，主要由以下核心类构成：

- **`MultiLevelCacheManager`**：继承 `AbstractCacheManager`，组合了本地（Caffeine）和远程（Redis）两个 `CacheManager`。它负责根据缓存名称从两端同时获取对应的 `Cache` 实例，只有两端都存在时才返回包装后的 `MultiLevelCache`，否则返回 `null`，保证多级缓存配置的一致性。
- **`MultiLevelCache`**：实现 `Cache` 接口，对上层屏蔽两级缓存差异，内部持有一个本地 `Cache` 和一个远程 `Cache`，以及按 key 维度的 `ReentrantLock` 映射，用于防止缓存击穿。
- **创建器（Creator）**：`CaffeineCacheManagerCreator`、`RedisCacheManagerCreator` 和 `MultiLevelCacheManagerCreator` 分别负责创建对应缓存管理器的单例实例，均采用双重检查锁定（DCL）保证全局唯一。
- **灵活配置组件**：`FlexibleCaffeineCacheManager` 允许为每个缓存名称独立配置 Caffeine 参数；`NamedRedisCacheManagerBuilderCustomizer` 为每个 Redis 缓存独立设置 TTL、键前缀等。

整体类图如下：

```
┌─────────────────────────────────────────────────────────────┐
│                   MultiLevelCacheManager                    │
│                (extends AbstractCacheManager)               │
│  ┌─────────────┐          ┌─────────────┐                 │
│  │ local: CacheManager  │  │ remote: CacheManager │        │
│  │ (Caffeine)           │  │ (Redis)              │        │
│  └─────────────┘          └─────────────┘                 │
└─────────────────────────┬───────────────────────────────────┘
                          │ getCache(name)
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     MultiLevelCache                         │
│                  (implements Cache)                         │
│  ┌─────────────┐          ┌─────────────┐                 │
│  │ local: Cache           │  │ remote: Cache           │    │
│  └─────────────┘          └─────────────┘                 │
│  ┌──────────────────────────────────┐                      │
│  │ locks: ConcurrentMap<Object,     │                      │
│  │          ReentrantLock>          │  ← 按 key 细粒度锁   │
│  └──────────────────────────────────┘                      │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 创建器（Creator）的设计模式

每个缓存管理器都有一个对应的 `CacheManagerCreator` 实现，它们均采用**双重检查锁定（DCL）** 实现线程安全的单例模式：

```java
public class MultiLevelCacheManagerCreator implements CacheManagerCreator<MultiLevelCacheManager> {
  private volatile MultiLevelCacheManager instance;
  @Override
  public MultiLevelCacheManager getInstance() {
    if (instance != null) return instance;
    synchronized (this) {
      if (instance != null) return instance;
      instance = newInstance();
    }
    return instance;
  }
  // newInstance() 组合底层创建器的实例
}
```

这种设计确保了在整个应用生命周期内只存在一个 `MultiLevelCacheManager` 实例，避免重复创建带来的资源浪费和配置冲突。

---

## 二、核心缓存策略（读写流程）

两级缓存的操作策略决定了一致性、性能和正确性。框架针对不同场景制定了清晰的规则。

### 2.1 读取（Get）操作：本地优先，远程兜底，回填本地

当调用 `get(key)` 或 `get(key, type)` 时：

1. 首先查询本地缓存（Caffeine）。
2. 若本地命中，直接返回。
3. 若未命中，查询远程缓存（Redis）。
4. 若远程命中，将值回填到本地缓存，并返回。
5. 若均未命中，返回 `null`。

```java
public ValueWrapper get(Object key) {
    ValueWrapper wrapper = local.get(key);
    if (wrapper != null) return wrapper;
    wrapper = remote.get(key);
    if (wrapper != null) {
        local.put(key, wrapper.get());   // 回填本地
        return wrapper;
    }
    return null;
}
```

此策略使得热点数据在第一次被远程加载后即可被后续请求从本地快速获取，大幅降低平均延迟。

### 2.2 带加载器的读取（get(key, Callable)）：细粒度锁防击穿

当缓存未命中且需要由 `Callable` 加载值时（如数据库查询），框架使用**按 key 的 ReentrantLock** 防止缓存击穿——即大量线程同时加载同一个 key。

流程如下：

1. 查本地，命中则返回。
2. 查远程，命中则回填本地并返回。
3. 若均未命中，获取当前 key 对应的锁（`locks.computeIfAbsent(key, k -> new ReentrantLock())`）。
4. 锁定后再次**双重检查**本地和远程（避免在等待锁期间其他线程已完成加载）。
5. 若仍未命中，调用 `remote.get(key, valueLoader)` 加载（此处 `remote.get` 也会利用 Redis 缓存或执行 valueLoader）。
6. 将加载得到的值写入本地缓存，返回。
7. 释放锁，并从 `locks` 中移除该 key 的锁对象，防止内存泄漏。

```java
ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
lock.lock();
try {
    // 双重检查...
    T value = remote.get(key, valueLoader);
    local.put(key, value);
    return value;
} finally {
    lock.unlock();
    locks.remove(key);
}
```

这种细粒度锁相比全局锁，显著提升了并发吞吐量，同时保证了同一 key 只被加载一次。

### 2.3 写入（Put）、删除（Evict）与清空（Clear）

- **`put(key, value)`**：同时写入本地和远程缓存。
- **`evict(key)`**：同时从本地和远程删除该键。
- **`clear()`**：同时清空两个缓存的所有条目。

同步双写保证了强一致性（至少操作上是原子性的），但会增加写入耗时。对于写频率较低、数据一致性要求高的场景，这种策略是合适的。

---

## 三、并发控制与性能优化

### 3.1 双重检查锁定（DCL）的全面应用

不仅创建器使用 DCL，`MultiLevelCache` 在带加载器的读取中也利用 DCL 减少锁竞争。这种模式在单例初始化和缓存加载场景下兼具安全性和低开销。

### 3.2 本地缓存的近实时响应

Caffeine 作为高性能本地缓存，提供纳秒级读写。通过合理配置 `maximumSize` 和 `expireAfterAccess`，可在内存占用和命中率之间取得平衡。

### 3.3 Redis 连接的优化

框架未直接涉及连接池配置，但底层 `RedisConnectionFactory`（如 Lettuce）可启用连接池和异步能力，减少网络开销。

---

## 四、灵活配置体系：为每个缓存独立调优

不同业务数据往往具有不同的访问模式和过期需求。框架通过 `NamedCacheProperties` 及配套组件，实现了**按缓存名称独立配置**，同时支持全局默认值。

### 4.1 配置模型：`NamedCacheProperties` 与 `NamedCacheOptions`

配置前缀为 `tutorials4j.cache.named`，包含：

- **`defaults`**（`NamedCacheOptions`）：全局默认配置，包括 TTL、是否允许空值、是否启用统计、Redis 键前缀、Caffeine 初始容量和最大容量等。
- **`caches`**（`Map<String, NamedCacheOptions>`）：每个缓存名称的专属配置，可覆盖默认值。

`NamedCacheOptions` 结构如下（简化）：

```java
public class NamedCacheOptions {
    private Duration timeToLive;
    private Boolean cacheNullValues;
    private Boolean enableStatistics;
    private RedisOptions redis;   // 含 cachePrefix
    private CaffeineOptions caffeine; // 含 initialCapacity, maximumSize, expireAfterAccess

    public void applyDefaults(NamedCacheOptions defaults) {
        // 仅当当前属性为 null 时从 defaults 继承
    }
}
```

### 4.2 FlexibleCaffeineCacheManager：为每个缓存定制 Caffeine 策略

Spring 官方 `CaffeineCacheManager` 所有缓存共享同一个 `Caffeine` 规格。`FlexibleCaffeineCacheManager` 通过重写 `createNativeCaffeineCache(String name)` 实现按名配置：

```java
@Override
protected Cache<Object, Object> createNativeCaffeineCache(String name) {
    NamedCacheOptions options = properties.getCaches().get(name);
    if (options == null) {
        options = properties.getDefaults(); // 降级使用默认
    }
    options.applyDefaults(properties.getDefaults()); // 合并全局
    Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
    CaffeineUtils.copyOption(caffeine, options); // 将配置复制到 Caffeine 构建器
    return caffeine.build();
}
```

这样，在配置文件中对不同缓存设置不同的 `maximumSize` 或 `expireAfterAccess` 即可生效：

```yaml
tutorials4j.cache.named:
  defaults:
    time-to-live: 60s
    caffeine.maximum-size: 1000
  caches:
    users:
      time-to-live: 120s
      caffeine.maximum-size: 500
    products:
      time-to-live: 300s
      caffeine.expire-after-access: 10m
```

### 4.3 Redis 缓存的按名称配置：`NamedRedisCacheManagerBuilderCustomizer`

对于 Redis 端，通过实现 `RedisCacheManagerBuilderCustomizer`，为每个命名缓存生成独立的 `RedisCacheConfiguration`：

```java
@Override
public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
    Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
    properties.getCaches().forEach((name, options) -> {
        options.applyDefaults(properties.getDefaults());
        RedisCacheConfiguration config = RedisUtils.fillConfiguration(builder.cacheDefaults(), options);
        configMap.put(name, config);
    });
    builder.withInitialCacheConfigurations(configMap);
}
```

这样，每个 Redis 缓存可以拥有不同的 TTL、键前缀和序列化方式。

### 4.4 扩展点

框架预留了 `RedisCacheManagerBuilderCustomizer` 和 `CacheManagerCustomizer<RedisCacheManager>` 扩展点，允许用户在构建 Redis 管理器前后注入自定义逻辑（如修改序列化、开启统计等）。Caffeine 端也可通过继承 `FlexibleCaffeineCacheManager` 或修改 `CaffeineUtils` 来增加新的配置项。

---

## 五、适用场景与最佳实践

### 5.1 适用场景

- **读多写少的热点数据**：如用户资料、商品详情、配置中心数据。
- **多实例部署的微服务**：通过 Redis 共享缓存，同时利用本地缓存降低节点间重复查询。
- **高并发系统**：本地缓存可承受极高 QPS，而 Redis 作为保底，细粒度锁防止击穿。

### 5.2 注意事项与建议

- **一致性权衡**：同步双写保证了操作原子性，但若 Redis 写入失败，本地和远程会不一致。可配合 TTL 自动过期，或使用消息队列异步修正。
- **内存规划**：需合理设置 Caffeine 的 `maximumSize`，防止堆内存溢出。可结合监控动态调整。
- **缓存名称一致性**：`MultiLevelCacheManager.getCache(name)` 要求两端都存在该名称，否则返回 `null`。务必在配置中同时定义本地和远程的对应缓存。
- **序列化选择**：Redis 缓存建议使用高效二进制序列化（如 Protobuf、Kryo），减少体积和编解码开销。
- **监控与调优**：开启 `enableStatistics`，通过 JMX 或 Micrometer 观察命中率，指导容量和过期策略调整。

---

## 六、总结

本文深入解析了一套基于 Caffeine 和 Redis 的两级缓存框架，其核心设计亮点可概括为：

1. **分层清晰**：`MultiLevelCacheManager` 负责管理组合，`MultiLevelCache` 负责操作策略，各司其职。
2. **策略合理**：本地优先、远程兜底、回填本地，充分利用本地缓存的高速；写入同步双写，保证一致性。
3. **并发安全**：双重检查锁定 + 按 key 细粒度锁，兼顾单例创建和缓存加载的线程安全与性能。
4. **配置灵活**：支持全局默认和按名称独立配置，适用于不同业务场景的差异化需求，且易于维护。
5. **易于扩展**：通过定制器接口和工具类，可方便地调整序列化、统计、过期策略等。

该框架已在多个生产项目中验证，显著降低了平均响应时间和数据库负载。开发者可根据自身业务特性调整 TTL、容量等参数，甚至替换本地缓存实现（如使用 Ehcache），或增加更多层级，实现更复杂的缓存策略。

多级缓存是高性能系统的利器，但其设计和运维也需细致考量。希望本文能为你构建稳定高效的缓存层提供有益参考。

