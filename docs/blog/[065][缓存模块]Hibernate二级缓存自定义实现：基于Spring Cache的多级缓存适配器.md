# [065][缓存模块]Hibernate二级缓存自定义实现：基于Spring Cache的多级缓存适配器

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

## 1. 引言

在Java企业级应用中，Hibernate作为最流行的ORM框架，其二级缓存（Second-Level Cache，L2 Cache）是提升性能的关键手段。然而，Hibernate原生支持Ehcache、Infinispan等缓存提供商，但对于Spring生态下广泛使用的Spring Cache抽象（如Caffeine、Redis）并无直接适配。本文分析一套自定义Hibernate二级缓存实现，该实现通过适配Spring Cache，统一了应用层缓存与ORM层缓存，并支持本地、远程、多级等多种缓存策略，实现了高灵活性和可扩展性。

## 2. 整体设计概览

该实现的核心思路是：  
- 实现Hibernate SPI接口 `DomainDataStorageAccess`，将缓存操作委托给Spring `Cache` 接口。  
- 通过 `RegionFactoryTemplate` 子类创建各类缓存区域（实体/集合、查询结果、时间戳）的存储访问实例。  
- 利用Spring Boot的 `HibernatePropertiesCustomizer` 注入自定义配置，使Hibernate能识别缓存类型。  
- 引入 `CacheType` 和 `CacheManagerCreatorCategory` 枚举，以支持多种缓存后端（Caffeine本地、Redis远程、多级混合）及其租户隔离变体。

整体类图关系如下：

```
┌──────────────────────────────────┐
│ CacheRegionFactoryTemplate       │
│ (extends RegionFactoryTemplate)  │
│  - cacheType: CacheType          │
│  - regionPrefix: String          │
│  + createDomainDataStorageAccess()│
│  + createQueryResults...()       │
│  + createTimestamps...()         │
└───────────────┬──────────────────┘
                │ creates
                ▼
┌──────────────────────────────────┐
│ CacheDomainDataStorageAccess     │
│ (implements DomainDataStorageAccess)│
│  - regionName                    │
│  - cacheType                     │
│  - cache (Spring Cache)          │
│  + getFromCache()                │
│  + putIntoCache()                │
│  + contains() / evictData()      │
└───────────────┬──────────────────┘
                │ uses
                ▼
┌──────────────────────────────────┐
│ CacheManagerCreatorFactory       │
│ (外部依赖)                       │
│  + findFirstCacheManagerCreator()│
│    -> CacheManagerCreator        │
│       -> getInstance()           │
│          -> getCache(regionName) │
└──────────────────────────────────┘
```

## 3. 核心组件详解

### 3.1 `CacheDomainDataStorageAccess` —— 桥接适配器

该类实现了Hibernate的 `DomainDataStorageAccess`，其职责是将所有缓存存取操作转换为对Spring `Cache` 的调用。

- **懒加载初始化**：`getCache()` 方法采用双重检查锁，保证 `Cache` 实例仅初始化一次，且线程安全。
- **缓存实例获取**：通过 `CacheManagerCreatorFactory` 根据 `CacheType` 对应的 `CacheManagerCreatorCategory` 链获取首个可用的 `CacheManager`，再从其中获取指定 `regionName` 的 `Cache` 对象。该设计支持降级（例如，若租户多级不可用，则回退至普通多级）。
- **键转换**：`wrapper(Object key)` 方法对 `QueryKey` 特殊处理，使用其 `hashCode` 作为缓存键，避免查询缓存键过于复杂或不可序列化的问题；其他键直接转为字符串。
- **操作实现**：`getFromCache`、`putIntoCache`、`contains`、`evictData` 分别委托给 `Cache` 的对应方法，清晰简洁。

**注意点**：`release()` 方法为空实现，因为Spring Cache实例由容器管理，无需手动释放。

### 3.2 `CacheRegionFactoryTemplate` —— 区域工厂

该类扩展自Hibernate的 `RegionFactoryTemplate`，负责创建三种缓存区域的存储访问对象：

- **实体/集合区域**（`createDomainDataStorageAccess`）
- **查询结果区域**（`createQueryResultsRegionStorageAccess`）
- **时间戳区域**（`createTimestampsRegionStorageAccess`）

三者均使用 `CacheDomainDataStorageAccess`，并传入统一的 `regionName`（允许添加前缀）和 `cacheType`。

**配置加载**：`prepareForUse` 方法从 `configValues` 中读取两个自定义设置：
- `tutorials4j.hibernate.cache_type`：值为 `CacheType` 枚举。
- `hibernate.cache.region_prefix`：标准Hibernate设置，用于为所有区域名称添加前缀，便于多应用共享缓存时隔离。

**生命周期**：`releaseFromUse` 也为空，因为无需额外资源释放。

### 3.3 `CacheRegionHibernatePropertiesCustomizer` —— Spring Boot集成

在Spring Boot环境中，通过实现 `HibernatePropertiesCustomizer` 接口，可以将我们自定义的属性注入到Hibernate配置中。该类的 `customize` 方法将 `HibernateDataProperties` 中的 `secondLevelCacheType` 设置到 `SimpleAvailableSettings.CACHE_TYPE` 键下，从而在 `CacheRegionFactoryTemplate` 初始化时被读取。

这一机制使得外部配置文件（如 `application.yml`）可以轻松控制缓存类型。

### 3.4 配置常量 `SimpleAvailableSettings`

定义自定义配置键 `tutorials4j.hibernate.cache_type`，它扩展了Hibernate原生的 `AvailableSettings`，以便在代码中统一引用。

### 3.5 缓存类型枚举 `CacheType` 与 `CacheManagerCreatorCategory`

`CacheType` 定义了三种逻辑缓存模式：

| 类型 | 代码 | 说明 | 优先采用的CreatorCategory链 |
|------|------|------|----------------------------|
| LOCAL | 1 | 仅本地缓存（如Caffeine） | [TENANT_CAFFEINE, CAFFEINE] |
| REMOTE | 2 | 仅远程缓存（如Redis） | [REDIS] |
| MULTI_LEVEL | 3 | 本地+远程多级缓存 | [TENANT_MULTI_LEVEL, MULTI_LEVEL] |

每个 `CacheType` 关联一个 `CacheManagerCreatorCategory` 数组，按优先级排列。`CacheManagerCreatorFactory` 会按顺序尝试创建，直到成功。这种设计允许优雅降级：当首选策略（如租户隔离）不可用时，自动切换到通用策略。

`CacheManagerCreatorCategory` 枚举则枚举了五种具体的缓存管理器创建策略，包括带租户和不带租户的变体，为多租户场景提供了原生支持。

## 4. 缓存键处理的细节与考量

Hibernate的缓存键可能为复杂对象（尤其是 `QueryKey`），直接使用 `toString()` 或对象引用会导致缓存命中率低或序列化问题。本实现中：
- 对于 `QueryKey`，使用 `hashCode()` 作为键，因为 `QueryKey` 重写了 `hashCode` 和 `equals`，确保语义上相同的查询产生相同的哈希值。
- 对于其他键（通常为实体ID或集合角色），`String.valueOf(key)` 足以满足一般情况。

**潜在风险**：`QueryKey.hashCode()` 可能产生冲突，但对于大多数场景是可接受的。若需要更强的唯一性，可考虑使用序列化后的字节数组或Base64编码。

## 5. 与Spring Cache的集成优势

- **统一缓存抽象**：应用层已使用Spring Cache注解（如 `@Cacheable`）时，Hibernate二级缓存可共用相同的CacheManager，减少配置冗余。
- **多级缓存支持**：通过 `CacheType.MULTI_LEVEL` 可实现本地Caffeine + 远程Redis的组合，兼顾性能与一致性（需配合消息通知机制）。
- **租户隔离**：通过 `TENANT_*` 变体，可以为多租户应用提供独立的缓存命名空间，避免数据混淆。
- **热切换能力**：仅需修改配置项即可切换缓存策略，无需改动代码。


## 6. 性能与扩展性分析

- **性能**：本地缓存（Caffeine）具有微秒级响应，远程缓存（Redis）为毫秒级，多级缓存可先查本地，未命中再查远程，显著降低网络开销。
- **扩展性**：通过 `CacheManagerCreatorFactory` 的SPI机制，可轻松添加新的缓存提供商（如Memcached、Hazelcast），只需实现对应的 `CacheManagerCreator` 并注册即可。
- **限制**：由于 `release()` 为空，某些需要显式关闭资源的缓存提供商可能需要额外处理，但Spring Cache通常由容器管理生命周期，影响不大。

## 7. 总结

本文分析了一套定制化Hibernate二级缓存适配器，其巧妙地将Hibernate SPI与Spring Cache抽象结合，提供了高度灵活且可配置的缓存方案。核心价值在于：

- 实现了Hibernate与Spring缓存生态的无缝集成。
- 支持本地、远程、多级及租户隔离等多种缓存策略，适应不同业务场景。
- 利用Spring Boot的自动配置机制，降低使用门槛。

该设计充分展现了面向接口编程和策略模式的优势，为需要精细控制ORM缓存的开发者提供了优秀范例。未来可进一步扩展，如增加缓存统计、支持动态调整缓存策略等，持续优化性能。
