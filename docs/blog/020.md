# [020][缓存模块]基于 BeanCreator 的缓存管理器创建器模式设计与实践

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework


## 1. 引言

在复杂的 Spring 应用架构中，缓存管理器的创建与管理是一项常见且关键的任务。不同场景可能需要不同的缓存实现（Caffeine、Redis、多级缓存），甚至需要支持租户隔离等高级特性。传统的做法是在配置类中直接 `new` 出缓存管理器实例，并手动处理单例缓存。这种方式会导致配置代码臃肿、单例逻辑重复、难以测试等问题。

为了解决这些问题，我们设计了一套基于 `BeanCreator` 函数式接口的创建器体系。该体系将缓存管理器的**创建逻辑**、**单例缓存**和**类型信息**进行抽象，形成了可复用、可组合、可扩展的创建器组件。本文将从 `BeanCreator` 接口设计出发，逐步分析其在缓存管理器创建场景下的具体实现与应用，并展示如何通过这一模式优雅地支持 Caffeine、Redis、多级缓存以及租户隔离。

---

## 2. `BeanCreator`——统一的 Bean 创建契约

`BeanCreator` 是一个函数式接口，定义了三种核心操作：

```java
@FunctionalInterface
public interface BeanCreator<T> {
    T getInstance();               // 获取单例实例（优先从缓存获取）
    default T newInstance() {      // 直接创建新实例，不走缓存
        throw new UnsupportedOperationException();
    }
    default Class<T> getBeanClass() { // 返回 Bean 的实际类型
        throw new UnsupportedOperationException();
    }
}
```

- **`getInstance()`**：使用者获取实例的唯一入口。实现者通常在此方法中实现**单例缓存逻辑**（如双重检查锁定），保证整个应用上下文只有一个目标实例。
- **`newInstance()`**：绕过缓存直接创建新实例，用于需要全新实例的场合（如单元测试或强制重建）。默认抛出异常，由子类按需重写。
- **`getBeanClass()`**：返回创建的 Bean 的精确 `Class` 类型，便于框架进行类型识别或元数据处理。

该接口的设计遵循了**模板方法模式**与**工厂模式**的混合思想：上层调用者无需关心实例如何创建、是否单例，只需调用 `getInstance()`；具体创建细节交由子类实现。

## 3. 缓存管理器创建器体系

针对 Spring 的 `CacheManager` 抽象，我们定义了子接口 `CacheManagerCreator<T extends CacheManager>`，它仅仅是 `BeanCreator<T>` 的标记性继承，表示所有缓存管理器的创建器都应遵循这一套行为规范。

随后，我们为每种缓存实现编写了对应的创建器：

| 创建器类 | 管理的缓存管理器类型 | 特点 |
|---------|---------------------|------|
| `CaffeineCacheManagerCreator` | `CaffeineCacheManager` | 基于 Caffeine 的本地缓存，支持 YAML 配置 |
| `RedisCacheManagerCreator` | `RedisCacheManager` | 基于 Redis 的分布式缓存，支持自定义器链 |
| `MultiLevelCacheManagerCreator` | `MultiLevelCacheManager` | 组合 Caffeine（L1）和 Redis（L2）的多级缓存 |
| `TenantCaffeineCacheManagerCreator` | `TenantCaffeineCacheManager` | 租户隔离的 Caffeine 缓存（装饰器模式） |
| `TenantMultiLevelCacheManagerCreator` | `MultiLevelCacheManager` | 租户隔离的多级缓存 |

这些创建器都采用了**双重检查锁定（Double-Checked Locking）** 实现线程安全的单例模式，例如 `CaffeineCacheManagerCreator` 中的核心逻辑：

```java
public CaffeineCacheManager getInstance() {
    if (instance != null) return instance;
    synchronized (this) {
        if (instance != null) return instance;
        instance = newInstance();
    }
    return instance;
}
```

这种写法既保证了第一次访问后的高性能（无锁读取），又确保了多线程环境下的唯一性。同时，每个创建器都正确实现了 `newInstance()` 和 `getBeanClass()` 方法，为框架或上层模块提供了完整的实例化能力。

## 4. 各创建器实现亮点分析

### 4.1 `CaffeineCacheManagerCreator`

- **依赖注入**：通过构造函数接收 `CacheCaffeineProperties` 配置对象和 `Caffeine<Object, Object>` 构造器实例。
- **灵活扩展**：内部使用了 `FlexibleCaffeineCacheManager`（非标准 Spring 类，但可以看出对配置的精细控制），允许针对不同缓存名称设置差异化过期策略（见 YAML 中的 `named-caches`）。
- **类型暴露**：`getBeanClass()` 返回 `CaffeineCacheManager.class`，符合面向接口编程的原则。

### 4.2 `RedisCacheManagerCreator`

- **复杂初始化**：需要 `RedisConnectionFactory`、配置属性、两个自定义器列表（`RedisCacheManagerBuilderCustomizer` 和 `CacheManagerCustomizer<RedisCacheManager>`）。
- **配置填充**：通过 `RedisUtils.fillConfiguration()` 根据 properties 设置默认的序列化、TTL、空值缓存等。
- **自定义器链**：先对 `RedisCacheManagerBuilder` 应用 builder 级 customizer，再对构建完成的 `RedisCacheManager` 应用 manager 级 customizer，提供了极高的定制自由度。

### 4.3 `MultiLevelCacheManagerCreator`

- **组合模式**：它不自己创建底层的 Caffeine 和 Redis 管理器，而是依赖传入的两个创建器：
  ```java
  return new MultiLevelCacheManager(
      caffeineCacheManagerCreator.getInstance(),
      redisCacheManagerCreator.getInstance()
  );
  ```
- **无状态**：自身只负责组合，真正的缓存管理器创建和单例维护委托给内部创建器。这体现了**单一职责原则**和**组合优于继承**。

### 4.4 租户感知创建器

`TenantCaffeineCacheManagerCreator` 和 `TenantMultiLevelCacheManagerCreator` 展示了如何在不修改原有创建器代码的前提下实现租户隔离：

- `TenantCaffeineCacheManager` 装饰了普通的 `CaffeineCacheManager`，在缓存操作时自动注入租户标识。
- 租户创建器直接复用原有的 `CaffeineCacheManagerCreator` 实例，仅通过装饰器包装返回。
- `TenantMultiLevelCacheManagerCreator` 更是组合了租户级 Caffeine 创建器和普通 Redis 创建器，实现了**一级缓存租户隔离，二级缓存共享**的混合策略。

这种设计使得租户能力的引入变得非侵入，符合开闭原则。

## 5. 在 Spring 配置中的落地

示例文件 `CacheableConfig` 展示了如何在 Spring 应用中使用这些创建器：

```java
@EnableCaching
@Configuration
@Profile("cacheable")
public class CacheableConfig implements CachingConfigurer {
    @Autowired private RedisCacheManagerCreator redisCacheManagerCreator;

    @Bean
    CaffeineCacheManager caffeineCacheManager(CaffeineCacheManagerCreator creator) {
        return creator.getInstance();   // 通过创建器获取单例
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return redisCacheManagerCreator.getInstance(); // 设为默认 CacheManager
    }
}
```

- **Profile 激活**：`@Profile("cacheable")` 使得该配置仅在特定环境生效。
- **显式 Bean 注册**：虽然 `CaffeineCacheManagerCreator` 本身可能已被 Spring 管理，但配置类仍然通过 `@Bean` 将 `creator.getInstance()` 的结果注册为 Spring Bean，这样做的好处是：
  - 让 Spring 容器也能感知到该缓存管理器（例如用于 `@Cacheable` 的 `cacheManager` 属性引用）。
  - 保持与标准 Spring Boot 自动配置的兼容性。
- **实现 `CachingConfigurer`**：重写 `cacheManager()` 方法可以全局指定默认的 `CacheManager`，这里使用了 Redis 缓存管理器作为默认实现。

而在服务层，`CaffeineCacheableService` 通过 `@CacheConfig(cacheManager = "caffeineCacheManager")` 显式指定使用 Caffeine 缓存，而 `RedisCacheableService` 没有指定，因此会使用默认的 Redis 管理器。这种灵活性正是得益于创建器模式——我们可以轻松地在不同 Service 中切换不同的缓存实现，而无需修改业务逻辑。

## 6. 配置驱动的缓存策略

`application-cacheable.yml` 展示了如何通过外部配置精细化控制每种缓存管理器的行为：

```yaml
tutorials4j:
  cache:
    caffeine:
      expire-after-write: 2m        # 全局默认
      named-caches:
        users:
          expire-after-write: 5s     # 针对 users 缓存单独配置
    redis:
      timeToLive: 2m
      keyPrefix: share
      named-caches:
        users:
          timeToLive: 10s
          keyPrefix: user
        orders:
          timeToLive: 20s
          cacheNullValues: true
```

这些配置会被 `CacheCaffeineProperties` 和 `CacheRedisProperties` 加载，并最终由各创建器在 `newInstance()` 时使用。`CaffeineCacheManagerCreator` 通过 `FlexibleCaffeineCacheManager` 支持每个缓存名称独立配置过期时间；`RedisCacheManagerCreator` 则通过 `RedisCacheConfiguration` 的个性化能力实现了类似效果。这种**声明式配置 + 创建器封装**的模式，极大地减少了样板代码。

## 7. 总结

`BeanCreator` 及其在缓存管理器场景下的实现，提供了一种优雅、可扩展的创建器模式。它具有以下优点：

- **职责清晰**：创建逻辑、单例控制、类型信息三者分离。
- **线程安全**：所有创建器都使用成熟的双重检查锁定模式，无需调用方额外同步。
- **可组合性**：多级缓存创建器直接依赖子创建器，租户创建器通过装饰增强，无需修改原有类。
- **配置友好**：与 Spring 的 `@ConfigurationProperties` 无缝集成，支持细粒度缓存策略。
- **测试友好**：可通过 Mock 创建器或直接调用 `newInstance()` 进行单元测试。

该模式不仅适用于缓存管理器，也可以推广到任何需要复杂创建逻辑且需要单例保证的组件（如数据源、连接池、消息生产者等）。通过抽象出 `BeanCreator` 接口，我们为整个框架的组件创建提供了统一的语义，降低了系统复杂度，提高了代码的可维护性和可测试性。

在实际项目中，建议将这种创建器模式与 Spring 的依赖注入容器结合使用：让创建器本身成为 Spring Bean，并在需要实例的地方注入创建器并调用 `getInstance()`。这样做既利用了 Spring 的生命周期管理，又保留了手工控制单例的灵活性，是一种值得推广的实践。

阅读最新文章，请关注我的微信公众号`杨运交` 