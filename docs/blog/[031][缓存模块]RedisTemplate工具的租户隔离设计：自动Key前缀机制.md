# [031][缓存模块]RedisTemplate工具的租户隔离设计：自动Key前缀机制

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在多租户SaaS系统中，不同租户的数据必须严格隔离。当多个租户共享同一套Redis缓存时，如何保证缓存Key不会冲突？本文以一个轻量级框架的实现为例，分析其利用租户上下文自动为所有缓存Key添加租户前缀的设计思路。

## 一、背景与需求

假设系统中有租户A和租户B，他们都访问同一个缓存数据项 `user:123`。若不加以区分，租户A将可能读到租户B的用户信息，造成严重的数据泄露。解决方案通常有两种：

1. **为每个租户部署独立Redis实例** —— 隔离彻底但成本高。
2. **在Key中嵌入租户标识** —— 共享实例但Key自动区分。

本文分析的代码采用了第二种方案，且实现方式对业务代码完全透明：开发者无需手动拼接租户ID，只需在请求入口设置租户上下文，框架便会自动为所有缓存Key添加租户前缀。

## 二、核心组件与租户前缀生成

### 2.1 租户上下文持有者

代码中使用了 `TenantContextHolder.get()` 来获取当前租户标识。这是一个典型的基于`ThreadLocal`的工具类，其实现不在本次代码片段中，但作用非常清晰：返回当前请求对应的租户ID（例如 `"tenantA"`）。

### 2.2 前缀策略工厂 `RedisUtils`

`RedisUtils` 接口中定义了静态方法 `defaultCacheKeyPrefix()`，它返回一个 `CacheKeyPrefix` 函数式接口实例：

```java
static CacheKeyPrefix defaultCacheKeyPrefix() {
    return name -> TenantContextHolder.get() + ":" + name + "::";
}
```

- `name`：缓存名称，例如 `"users"`
- 返回值示例：若租户ID为 `"acme"`，缓存名为 `"users"`，则生成的前缀为 `"acme:users::"`

### 2.3 带自定义二级前缀的重载方法

`defaultCacheKeyPrefix(String prefix)` 允许在租户前缀和缓存名之间再插入一段自定义前缀，例如：

```java
defaultCacheKeyPrefix("v2")  // 租户acme，缓存users -> "acme:v2:users::"
```

这种设计支持更细粒度的Key版本管理或业务分类。

## 三、自动前缀的注入方式

### 3.1 自定义Key序列化器 `PrefixKeyStringRedisSerializer`

该类继承自 `StringRedisSerializer`，并重写了 `serialize` 方法：

```java
public byte[] serialize(String value) {
    return super.serialize(RedisUtils.defaultCacheKeyPrefix().compute(value));
}
```

- 传入的 `value` 是原始的Key（如 `"user:123"`）
- `compute(value)` 将原始Key转换为带租户前缀的完整Key（如 `"acme:userCache::user:123"`）
- 然后调用父类的字符串序列化逻辑

这意味着：**任何使用该序列化器的 `RedisTemplate`，在写入或读取Key时，都会自动加上租户前缀**。

### 3.2 配置类中的装配 `RedisConfiguration`

`RedisConfiguration` 内部有一个条件配置类 `InnerConfiguration`，它会在 `StringRedisTemplate` 和 `RedisTemplate` Bean存在时自动执行：

```java
@PostConstruct
public void postConstruct() {
    PrefixKeyStringRedisSerializer serializer = new PrefixKeyStringRedisSerializer();
    stringRedisTemplate.setKeySerializer(serializer);
    stringRedisTemplate.setHashKeySerializer(serializer);

    redisTemplate.setKeySerializer(serializer);
    redisTemplate.setHashKeySerializer(serializer);
}
```

- 同时替换了普通Key和Hash结构的Key序列化器
- 由于 `@PostConstruct` 在Bean初始化后执行，所有后续操作都会自动生效

### 3.3 缓存管理器的前缀配置

除了 `RedisTemplate`，Spring Cache抽象层（`@Cacheable`等）也使用了相同的前缀策略。在 `RedisUtils.fillConfiguration` 方法中：

```java
configuration = configuration.computePrefixWith(RedisUtils.defaultCacheKeyPrefix());
```

该方法被用于构建 `RedisCacheManager` 的每个缓存配置，确保通过注解生成的缓存Key也自动携带租户前缀。

## 四、租户隔离效果演示

假设两个租户同时调用以下代码：

```java
// 租户A（tenantId = "a"）
redisTemplate.opsForValue().set("user:1", "Alice");
// 租户B（tenantId = "b"）
redisTemplate.opsForValue().set("user:1", "Bob");
```

由于序列化器自动添加前缀，Redis中实际存储的Key为：

- `a:userCache::user:1` → `"Alice"`
- `b:userCache::user:1` → `"Bob"`

租户A永远无法读取到租户B的数据，且业务层代码完全无感知。

## 五、设计优点与注意事项

### 优点

1. **无侵入性**：业务开发者无需关心租户隔离，只需确保请求入口设置了 `TenantContextHolder`。
2. **统一管理**：所有缓存Key的前缀规则集中定义在 `RedisUtils` 中，易于调整。
3. **支持自定义二级前缀**：允许不同模块或版本使用不同的Key前缀，避免升级时的缓存混乱。
4. **透明覆盖**：通过替换 `RedisTemplate` 的序列化器，对已有代码零修改。

### 注意事项

- **租户上下文必须正确传递**：在异步线程或消息消费场景下，需要手动拷贝租户ID到子线程（可使用装饰器或TTL）。
- **前缀长度影响内存**：过长的租户ID + 缓存名会产生较长的Key，但相比隔离性带来的收益通常可接受。
- **清除缓存时需注意**：`RedisTemplate.keys("acme:*")` 扫描时需指定正确前缀，避免跨租户删除。

## 六、总结

本文分析的代码通过**自定义Key序列化器 + 租户上下文**的组合，实现了透明、高效的Redis缓存租户隔离。其核心思路非常简洁：在Key进入Redis之前“悄悄”加上租户前缀。对于希望共享Redis实例但又必须保证数据隔离的多租户系统，这种模式极具参考价值。

该设计不仅适用于租户隔离，也可推广至环境隔离（dev/test/prod）、应用标识注入等场景，是一种通用的缓存Key“装饰器”模式。

阅读最新文章，请关注我的微信公众号`杨运交` 