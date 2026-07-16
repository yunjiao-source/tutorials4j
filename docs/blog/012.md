# [012][缓存模块]基于 Spring Cache 的缓存操作模版，支持Caffeine缓存, Redis缓存及两级缓存

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

## 1. 引言

在微服务与高并发场景下，缓存是提升系统性能的关键手段。Spring Framework 提供了 `Cache` 和 `CacheManager` 抽象，但实际业务中往往需要融合本地缓存（如 Caffeine）和分布式缓存（如 Redis），形成多级缓存，同时还要支持异步加载、租户隔离等高级特性。本文介绍一款轻量级缓存框架的设计与实现，它基于 Spring Cache 抽象，通过工厂模式、模板方法模式，提供灵活、可扩展的缓存操作入口。

## 2. 整体架构

框架核心包含以下几个模块：

- **缓存管理器创建器工厂** (`CacheManagerCreatorFactory`)：单例工厂，注册多种 `CacheManagerCreator`，根据类型获取对应的 `Cache` 实例。
- **缓存操作模板** (`CacheTemplate` 及其抽象实现)：封装常见的缓存操作（get、put、delete、异步加载等），为业务层提供统一 API。
- **具体缓存模板**：`CaffeineCacheTemplate`、`RedisCacheTemplate`、`MultiLevelCacheTemplate`，分别对应本地、远程、多级缓存场景。
- **示例业务类**：`CaptchaCacheTemplate` 展示如何使用多级缓存实现验证码服务。

整体类图关系如下：

```
CacheManagerCreatorFactory (单例)
    │
    ├── 持有 List<CacheManagerCreator<?>>
    │
    └── 提供 getXxxCache(cacheName) : Cache

CacheTemplate<K,V> (接口)
    │
    └── AbstractCacheTemplate<K,V> (抽象类)
            │
            ├── CaffeineCacheTemplate<K,V>
            ├── RedisCacheTemplate<K,V>
            └── MultiLevelCacheTemplate<K,V>
                    │
                    └── CaptchaCacheTemplate (业务示例)
```

## 3. 核心组件分析

### 3.1 CacheManagerCreatorFactory —— 缓存管理器的统一工厂

`CacheManagerCreatorFactory` 是一个单例类，其职责是：**根据缓存的“类型标识”获取对应的 `CacheManagerCreator`，再由该创建器提供具体的 `Cache` 实例**。设计要点如下：

- **单例 + 手动注册**：通过 `setCacheManagerCreators(List<...>)` 注入所有创建器，支持 Spring 配置或手动调用。
- **类型定位**：每个 `CacheManagerCreator` 实现类都有一个全限定类名（如 `DefaultConsts.CLASS_MULTI_LEVEL_CACHE_MANAGER_CREATOR`），工厂根据类名通过反射或直接类比较来查找。
- **降级与容错**：例如 `getCaffeineCache` 方法会先尝试获取带租户支持的创建器，再回退到普通创建器；`getMultiLevelCache` 中有一段重复代码（疑似遗留 bug，但不影响理解设计意图）。
- **异常处理**：未找到对应创建器时抛出 `CacheManagerInstanceNotFoundException`。

这种设计将“哪些缓存管理器可用”与“如何使用它们”解耦，新增一种缓存类型（如 Hazelcast）只需增加对应的 `CacheManagerCreator` 实现并注册到工厂即可，无需修改业务模板代码。

### 3.2 CacheTemplate —— 缓存操作的标准接口

`CacheTemplate<K, V>` 定义了以下核心方法：

| 方法 | 说明 |
|------|------|
| `put(K, V)` | 存入键值对 |
| `putIfAbsent(K, V)` | 原子性不存在则存入 |
| `get(K)` | 获取值，可能返回 null |
| `get(K, Callable<V>)` | 获取或加载 |
| `delete(K)` | 删除 |
| `valueGenerator(K)` | 抽象方法：由子类定义如何生成默认值 |
| `create(K)` | 默认方法：生成值并存入 |
| `retrieve(K)` / `retrieve(K, Supplier<CF>)` | 异步获取/加载（底层需支持 Spring 5.2 后的 `Cache.retrieve`） |

该接口既支持同步阻塞操作，也支持 `CompletableFuture` 异步编程，适应现代响应式业务需求。

### 3.3 AbstractCacheTemplate —— 模板方法基类

`AbstractCacheTemplate` 实现了 `CacheTemplate` 的大多数方法，并实现了 `SmartInitializingSingleton` 接口。其关键设计是：

- **延迟初始化**：在 Spring 容器启动完成后（`afterSingletonsInstantiated` 回调）调用抽象方法 `initCache()`，由子类负责从工厂中获取实际的 `Cache` 对象。
- **委托模式**：所有缓存操作直接委托给底层的 `Cache` 实例，减少重复代码。
- **类型安全**：`get(K key)` 通过 `getValueClass()` 获取泛型类型，确保返回值的正确转换。

### 3.4 三种具体缓存模板

| 类名 | initCache() 实现 | 适用场景 |
|------|------------------|----------|
| `CaffeineCacheTemplate` | `factory.getCaffeineCache(cacheName)` | 纯本地缓存，高性能，适合单机频繁读取、数据量可控的场景 |
| `RedisCacheTemplate` | `factory.getRedisCache(cacheName)` | 纯分布式缓存，适合多实例共享数据、持久化需求 |
| `MultiLevelCacheTemplate` | `factory.getMultiLevelCache(cacheName)` | 一级 Caffeine + 二级 Redis，兼顾性能与一致性 |

这三种模板均保持对工厂的松耦合，未来若需要更换缓存实现（例如从 Redis 切换到 Redisson），只需修改对应的 `CacheManagerCreator` 实现，业务层无需改动。

### 3.5 示例：验证码缓存服务

`CaptchaCacheTemplate` 是一个具体的业务示例，继承 `MultiLevelCacheTemplate<String, String>`，并实现了：

- **缓存名称**：`"captcha"`
- **值类型**：`String.class`
- **值生成器**：`RandomStringUtils.secure().nextAlphanumeric(4)` → 4 位随机串
- **业务方法**：`check(key, inputValue)` 用于校验验证码

通过该示例可以看出，业务开发者只需继承对应的缓存模板、指定泛型、实现少量抽象方法，就能获得完整的缓存能力，代码极其简洁。

## 4. 工作流程

以验证码服务为例，运行时流程如下：

1. **Spring 启动**：扫描到 `CaptchaCacheTemplate` 并实例化。
2. **单例初始化后**：调用 `afterSingletonsInstantiated()` -> `initCache()` -> 通过 `CacheManagerCreatorFactory` 获取多级缓存实例（名称 `captcha`）。
3. **业务调用**：
   - 生成验证码：`captchaCacheTemplate.create(phoneNumber)` → 调用 `valueGenerator` 生成随机码 → 调用 `multilevelCache.put(phoneNumber, code)`。
   - 校验验证码：`captchaCacheTemplate.get(phoneNumber)` 获取已存储的验证码，与用户输入比对。

整个过程中，开发者无需关心底层是本地缓存还是 Redis，也不需要编写任何 RedisTemplate 或 Caffeine 构造代码。

## 5. 设计亮点与可扩展性

### 5.1 工厂 + 创建器模式，隔离具体实现

`CacheManagerCreatorFactory` 并不直接创建 `CacheManager`，而是委托给各个 `CacheManagerCreator`。这样每个缓存类型（Caffeine、Redis、多级）可以有自己的初始化逻辑（例如连接池、过期策略、序列化方式），符合**开闭原则**。

### 5.2 模板方法 + 委托，极大减少重复代码

所有 `CacheTemplate` 实现类都复用 `AbstractCacheTemplate` 的通用操作，子类唯一需要关心的是如何初始化 `cache` 对象以及提供 `valueGenerator`。相比之下，传统做法每个业务类都要注入 `CacheManager`、根据名称获取 `Cache`、处理异常，代码冗余严重。

### 5.3 异步 API 支持

`CacheTemplate` 提供了 `CompletableFuture<?> retrieve(K key)` 方法，对应 Spring 5.2 引入的 `Cache.retrieve(Object)`。这允许业务层以非阻塞方式获取缓存，结合异步编程模型（如 WebFlux）可以大幅提升吞吐量。

### 5.4 适配 Spring 生命周期

通过实现 `SmartInitializingSingleton`，确保在 Spring 完成所有单例 Bean 的实例化、属性注入、初始化后，再获取底层 `Cache`。这避免了 `Cache` 未就绪就被使用的问题。

## 6. 使用指南

### 6.1 引入依赖与配置

- 确保项目中已有 Spring Cache 依赖以及相应实现（Caffeine、Redis）。
- 配置好 `CacheManager` Bean，并创建对应的 `CacheManagerCreator` 实现类（框架提供默认实现，本文未展示）。
- 在 Spring 配置中，将各个 `CacheManagerCreator` 注入到 `CacheManagerCreatorFactory.INSTANCE`。

### 6.2 编写业务缓存类

```java
@Service
public class UserCacheTemplate extends CaffeineCacheTemplate<Long, User> {
    public UserCacheTemplate() {
        super("users");
    }

    @Override
    public Class<User> getValueClass() {
        return User.class;
    }

    @Override
    public User valueGenerator(Long userId) {
        // 从数据库加载
        return userDao.findById(userId);
    }
}
```

### 6.3 在业务代码中使用

```java
@Autowired
private UserCacheTemplate userCache;

public User getUser(Long id) {
    return userCache.get(id);  // 缓存命中则直接返回，未命中返回 null
}

public User getOrLoadUser(Long id) {
    return userCache.get(id, () -> userDao.findById(id));
}
```

## 7. 潜在改进点

- **重复代码**：`getMultiLevelCache` 方法中两次尝试获取同一个创建器，应删除冗余判断。
- **性能优化**：`getCacheManager(String className)` 使用反射和类全名匹配，可增加缓存 `Map<String, Class<?>>` 避免重复 `Class.forName`。
- **泛型擦除警告**：`AbstractCacheTemplate` 中 `putIfAbsent` 和 `retrieve` 等方法存在未检查的强制转换，可考虑引入 `Cache.get(key, TypeReference)` 方式提升类型安全。
- **异步加载的异常处理**：`retrieve(K, Supplier<CF<V>>)` 目前直接透传，实际可增加超时、降级等策略。

## 8. 总结

本文介绍了一款基于 Spring Cache 抽象、采用工厂模式与模板方法模式构建的缓存框架。该框架通过 `CacheManagerCreatorFactory` 统一管理多种缓存类型（Caffeine、Redis、多级），通过 `CacheTemplate` 系列类为业务提供简洁、类型安全的操作接口。示例 `CaptchaCacheTemplate` 展示了如何仅用少量代码实现验证码缓存服务。整个框架具有良好的扩展性和可维护性，适合在需要灵活缓存策略的企业级项目中推广使用。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e