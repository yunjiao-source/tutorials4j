# [013][缓存模块]基于Redis的计数器缓存模板设计——AbstractCounterCacheTemplate 技术解析

本项目代码:https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

## 1. 概述

在业务系统中，我们经常需要对某个行为或资源的访问次数进行统计，并设定一个上限阈值。例如：  
- 用户每日登录尝试次数限制（5 次）  
- 短信验证码发送频率限制（每分钟 1 次）  
- API 调用配额限制（每小时 1000 次）  

传统做法是使用数据库记录次数，但高频计数会带来较大的写入压力。利用 **Redis** 原子自增特性实现计数器，既能保证性能，又能借助缓存过期机制控制时间窗口。

`AbstractCounterCacheTemplate` 正是这样一个基于 Redis 的计数器模板抽象类，它继承自 `AbstractRedisCacheTemplate<String, Integer>`，提供了开箱即用的“计数 + 上限校验”能力。开发者只需继承该类并配置 `cacheName`，即可获得一个线程安全的、带有溢出异常的计数器。

## 2. 类结构设计

```java
public abstract class AbstractCounterCacheTemplate 
        extends AbstractRedisCacheTemplate<String, Integer> {
    private int maxTimes = 1;
    // 核心计数方法
    public int counting(String key) throws CounterOverflowException;
    public int counting(String key, int maxTimes) throws CounterOverflowException;
    public int counting(String key, int maxTimes, boolean useMd5);
}
```

### 2.1 继承体系

- `CacheTemplate<K,V>`：顶层接口，定义缓存基本操作（put、get、delete 等）。
- `AbstractCacheTemplate<K,V>`：抽象实现，利用 Spring 的 `Cache` 抽象完成大部分操作，并在 `SmartInitializingSingleton` 回调中初始化底层缓存。
- `AbstractRedisCacheTemplate<K,V>`：指定底层缓存来源为 Redis（通过 `CacheManagerCreatorFactory` 获取）。
- `AbstractCounterCacheTemplate`：进一步限定键类型为 `String`，值类型为 `Integer`，并固化计数器逻辑。

### 2.2 核心属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `maxTimes` | `int` | 默认最大计数次数，可通过构造或 setter 修改。默认值为 1。 |

## 3. 核心方法解析

### 3.1 值类型与生成器

```java
@Override
public Class<Integer> getValueClass() {
    return Integer.class;
}

@Override
public Integer valueGenerator(String key) {
    return 1;   // 首次计数时，缓存中存入 1
}
```

- `getValueClass()`：告知底层 `Cache` 实例在 `get(key, Class)` 时进行类型转换。
- `valueGenerator(String key)`：当缓存中不存在指定键时，`create(key)` 会调用该方法生成初始值并存入缓存。计数器场景下，首次计数后次数应为 1，因此返回 `1`。

### 3.2 计数方法重载

提供了三个 `counting` 重载方法，最终都委托给最完整的方法：

```java
public int counting(String key, int maxTimes, boolean useMd5) 
        throws CounterOverflowException
```

**执行流程**：

1. **参数校验**：`key` 不能为 `null`。
2. **键转换**：若 `useMd5 == true`，则使用 MD5 对原始键进行摘要，以减少 Redis 键的内存占用（适用于键较长、含特殊字符或敏感信息的场景）。
3. **获取当前计数值**：通过 `get(newKey)` 获取，若为 `null` 则视为 `0`。
4. **分支处理**：
   - 若当前值为 `0`（即首次计数）：调用 `create(newKey)`，内部调用 `valueGenerator` 将 `1` 存入缓存。
   - 若当前值 `>0`：执行 `put(newKey, index + 1)` 将次数加一。
     - 若加一前的 `index >= maxTimes - 1`，说明本次操作后次数将 **达到或超过** 上限，则抛出 `CounterOverflowException`，异常消息为 `String.valueOf(maxTimes)`。
5. **返回值**：`index + 1`（即本次计数后的新值）。

### 3.3 异常处理

`CounterOverflowException` 是自定义的运行时异常，调用方可根据需要捕获并处理（如提示用户“操作次数已达上限”）。

## 4. 使用示例

### 4.1 定义具体计数器类

```java
@Service
public class LoginAttemptCounter extends AbstractCounterCacheTemplate {
    public LoginAttemptCounter() {
        super("login:attempt");        // 指定 Redis 缓存名称
        setMaxTimes(5);                // 设置最多允许 5 次错误尝试
    }
}
```

### 4.2 在业务代码中计数

```java
@Service
public class AuthService {
    @Autowired
    private LoginAttemptCounter counter;

    public void login(String username, String password) {
        try {
            int currentTimes = counter.counting(username);
            // 正常登录验证...
        } catch (CounterOverflowException e) {
            throw new BusinessException("登录尝试次数已达上限，请稍后再试");
        }
    }
}
```

### 4.3 带独立上限的计数

```java
// 动态指定本次计数的上限（例如不同等级的 API 调用方配额不同）
int times = counter.counting(apiKey, 100, false);
```

### 4.4 对敏感键进行 MD5 处理

```java
// 原始键可能是手机号、邮箱等隐私信息，使用 MD5 摘要后作为 Redis 键
int times = counter.counting(userEmail, 10, true);
```

## 5. 注意事项与最佳实践

### 5.1 缓存过期策略

`AbstractCounterCacheTemplate` 本身未设置缓存过期时间，计数会一直累加直到被手动删除或 Redis 内存淘汰。  
实际使用时，应利用底层 Redis 缓存的 **TTL** 能力，例如在初始化缓存时设置过期时间（可重写 `create` 方法，或在获取 `Cache` 实例时配置默认过期）。推荐结合 `@Cacheable` 的 `expire` 属性或使用 `RedisCacheManager` 的默认配置。

### 5.2 线程安全与原子性

底层调用的是 Spring `Cache` 接口，对于 `putIfAbsent`、`get`、`put` 操作是否原子取决于具体实现。  
Redis 缓存实现（如 `RedisCache`）使用 `SET NX` 和 `INCR` 等原子命令。但当前代码逻辑包含 **get → 判断 → put** 三步，存在竞态条件（两个线程同时 get 到 `null`，都会执行 `create` 导致初始值被覆盖）。  
如果需要严格的原子性，建议改用 Redis 原生的 `INCR` 命令并配合 Lua 脚本校验上限。不过对于绝大多数非金融级计频场景，当前实现已经足够。

### 5.3 键值类型限制

- 键固定为 `String`，值固定为 `Integer`。如果需要计数为 `Long` 类型（可能超过 21 亿），可修改 `getValueClass()` 返回 `Long.class` 并调整 `valueGenerator` 返回 `1L`。

### 5.4 溢出语义说明

抛出异常时，**缓存中的值已经被加一**。调用方可以根据异常信息决定是否进行回滚（例如将计数减一），但通常业务上不需回滚，因为已经达到上限后再计数也应视为失败。

## 6. 总结

`AbstractCounterCacheTemplate` 是一个轻量级的、面向业务计数的缓存模板抽象类，它：

| 特点 | 描述 |
|------|------|
| ✅ **开箱即用** | 继承后只需指定 `cacheName` 和 `maxTimes` 即可获得完整计数能力 |
| ✅ **上限保护** | 达到阈值自动抛出 `CounterOverflowException` |
| ✅ **键安全** | 支持对原始键进行 MD5 摘要，适合敏感信息场景 |
| ✅ **灵活重载** | 可动态传入单次计数的上限值 |
| ✅ **基于 Redis** | 利用 Redis 高性能和持久化能力，适合分布式环境 |

**适用场景**：  
- 登录/密码尝试次数限制  
- 验证码发送频率控制  
- 用户操作配额（点赞、评论、下载等）  
- 开放平台 API 调用频控

**改进建议**：  
- 增加缓存过期时间（可以配合 `@CachePut` 的 `condition` 或自定义 TTL）  
- 考虑提供原子性更强的实现（Lua 脚本）  
- 支持递减或重置计数的方法

通过合理使用该模板，开发者可以快速为系统添加上限可控的计数器功能，同时保持代码简洁和可维护性。

阅读最新文章，请关注我的微信公众号： 杨运交 ，公众号ID： gh_31209a11b93e