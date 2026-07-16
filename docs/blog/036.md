# [036][缓存模块]基于 Redis 自定义缓存锁的设计与实现

本项目代码: https://gitee.com/yunjiao-source/tutorials4j/tree/master/framework

在高并发场景下，缓存（如 Redis）除了承担数据加速的职责，还经常被用来实现分布式协调原语，其中最典型的就是**分布式锁**。本文剖析一套轻量级、可扩展的 Redis 自定义锁组件——通过注解 + AOP + Lua 脚本，提供固定租期和自动续期两种模式，能够干净地嵌入业务代码，有效解决缓存击穿、重复计算、资源竞争等问题。

## 一、为什么需要“缓存锁”

当我们使用 Redis 作为缓存时，常会遇到以下痛点：

- **缓存击穿**：热点数据过期瞬间，大量请求同时回源 DB，造成数据库压力骤增。
- **重复计算**：多个节点同时执行同一耗时任务（如生成报表、刷新缓存），导致资源浪费。
- **资源互斥**：对共享资源（库存、唯一流水号）需要串行化操作。

传统方案是在业务代码中手工编写 `setnx`、`expire`、`del` 等 Redis 命令，不仅重复且容易遗漏边界条件（如忘记释放锁、未处理锁超时）。一个优雅的解法是利用 **自定义注解 + AOP** 将锁逻辑声明式地织入目标方法，就像使用 `@Cacheable` 一样简单。

下文将完整展示一个生产就绪的 Redis 分布式锁组件的设计思路和核心实现。

## 二、整体设计

### 2.1 设计目标

- **声明式锁**：通过 `@RedisLockable` 注解标注需要加锁的方法。
- **SpEL 动态 Key**：锁的 Key 支持方法参数表达式，灵活区分不同资源。
- **双模式支持**：
  - **固定租期**：锁持有超时自动释放，适合短时间任务。
  - **自动续期**：后台定时续期，避免长任务锁过期，类似 Redisson 的看门狗。
- **原子性保证**：加锁、解锁、续期均使用 Lua 脚本，避免竞态条件。
- **低侵入**：业务代码只需关注核心逻辑，锁的获取与释放完全由 AOP 管理。

### 2.2 架构组件

| 组件                     | 职责                                                         |
| ------------------------ | ------------------------------------------------------------ |
| `@RedisLockable`         | 方法级注解，配置前缀、SpEL key、过期时间、时间单位。         |
| `RedisLockableAspect`    | AOP 切面，解析注解，调用 `RedisLockService` 执行锁逻辑。     |
| `RedisLockService`       | 核心锁服务，封装 Lua 脚本，提供 `FixedLease` 和 `AutoRenewal` 两种内部类。 |
| `SpelMethodBasedExpressionEvaluator` | 解析 SpEL 表达式，将方法参数转换为动态 Key。       |

### 2.3 工作流程

1. 方法执行前，切面拦截到 `@RedisLockable` 注解。
2. 通过 SpEL 计算出完整的锁 Key（prefix + 动态部分）。
3. 根据 `expireTime` 判断模式：
   - `expireTime > 0` → 调用 `FixedLease`，指定租期执行任务。
   - `expireTime <= 0` → 调用 `AutoRenewal`，启用看门狗。
4. 锁获取成功则执行目标方法，最终自动释放锁（或续期直到完成）。

## 三、核心实现深度解析

### 3.1 自定义注解 `@RedisLockable`

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLockable {
    String prefix() default "";
    String key();                 // SpEL 表达式，如 "#userId"
    long expireTime() default -1;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
```

**设计要点**：
- `prefix` 用于区分业务模块，例如 `"order:"`、`"stock:"`。
- `key` 支持 SpEL，可访问方法参数、属性等，例如 `#dto.id`。
- `expireTime <= 0` 作为自动续期的约定开关，自然语义清晰。

### 3.2 AOP 切面：非侵入锁控制

```java
@Around("@annotation(redisLockable)")
public Object around(ProceedingJoinPoint joinPoint, RedisLockable redisLockable) {
    String dynamicKey = spelEvaluator.getValue(method, args, redisLockable.key(), String.class);
    String fullKey = redisLockable.prefix() + dynamicKey;
    
    if (redisLockable.expireTime() > 0) {
        return redisLockService.fixedLease()
            .doInLock(fullKey, Duration.of(expireTime, unit), () -> joinPoint.proceed());
    } else {
        return redisLockService.autoRenewal()
            .doInLock(fullKey, () -> joinPoint.proceed());
    }
}
```

**亮点**：切面与锁服务完全解耦，新增模式只需扩展 `RedisLockService`，无需修改切面。

### 3.3 加锁的 Lua 脚本

```lua
return redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2])
```

- `NX`：仅当 Key 不存在时设置，实现互斥。
- `PX`：毫秒级过期时间，避免死锁。
- 返回值 `OK` 表示加锁成功，否则表示锁已被占用。

### 3.4 自动续期（看门狗）

自动续期的核心是一个递归的 `TimerTask`：

```java
private void renewalLock(String lockKey, String lockId) {
    new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
            if (续期成功) {
                renewalLock(lockKey, lockId);  // 递归，形成持续续期链
            }
        }
    }, DEFAULT_EXPIRE_TIME.toMillis() / 3);   // 每 1/3 租期续期一次
}
```

**注意**：生产环境中建议使用 `ScheduledExecutorService` 替代 `Timer`，并考虑取消任务的机制。当前代码在锁释放后递归链条会自然终止（因为续期脚本返回 false），但 Timer 线程可能仍存活一段时间，这是一个可优化点。

### 3.5 解锁的 Lua 脚本

```lua
if redis.call('get', KEYS[1]) == ARGV[1] then
    return tostring(redis.call('del', KEYS[1]) == 1)
else
    return 'false'
end
```

**为什么要比对 value？**  
防止误删其他线程的锁。每个锁的 value 是一个 UUID（`IdUtil.fastSimpleUUID()`），只有持有者才能释放，这是分布式锁的安全红线。

## 四、使用示例

### 4.1 固定租期模式（防缓存击穿）

```java
@RedisLockable(prefix = "cache:refresh:", key = "#cacheName", expireTime = 5, timeUnit = TimeUnit.SECONDS)
public void refreshCache(String cacheName) {
    // 从 DB 加载最新数据并写入 Redis
}
```

保证同一时刻只有一个节点执行刷新任务，其他节点等待锁释放后直接从缓存读取。

### 4.2 自动续期模式（长任务保护）

```java
@RedisLockable(prefix = "report:gen:", key = "#reportId")  // expireTime = -1
public String generateLargeReport(String reportId) throws InterruptedException {
    // 可能耗时几分钟的任务，锁会自动续期
    return reportService.build(reportId);
}
```

无需担心任务未完成锁就过期，看门狗会在后台持续续期，任务结束后自动释放。

## 五、优缺点及生产环境改进建议

### ✅ 优点

1. **声明式**：业务代码零污染，锁逻辑与业务逻辑分离。
2. **灵活 Key**：SpEL 表达式覆盖绝大多数动态资源标识场景。
3. **双模式**：固定租期适合短任务，自动续期适合长任务，按需选择。
4. **原子操作**：Lua 脚本保证加锁/解锁的原子性，避免 race condition。
5. **安全释放**：基于 value 匹配的释放机制，杜绝锁误删。

### ⚠️ 缺点与改进方向

| 问题点                       | 改进建议                                                    |
| ---------------------------- | ----------------------------------------------------------- |
| `Timer` 续期导致线程泄露风险 | 改用 `ScheduledExecutorService`，并返回 `ScheduledFuture` 以便取消。 |
| 续期任务无法被显式停止       | 在 `unlock` 时增加一个取消续期的回调机制。                  |
| 没有重试机制                 | 增加重试策略（如重试间隔、最大次数），或提供阻塞等待的 API。 |
| 锁的可重入性缺失             | 扩展脚本支持重入计数，或结合 ThreadLocal 实现。             |
| 监控与告警缺失               | 添加 Micrometer 指标（加锁耗时、失败次数、续期次数）。      |

## 六、总结

本套基于 Redis 的自定义锁组件，通过注解 + AOP + Lua 脚本实现了生产可用的分布式锁，既解决了缓存场景下的资源互斥问题，也为更广义的并发控制提供了优雅的声明式方案。其设计思想——**将基础设施能力以声明式 API 暴露给上层业务**——值得在缓存、限流、幂等等场景中反复借鉴。

在实际使用时，建议根据业务压测调整租期时长，并配合 Redis 集群模式保证锁服务本身的高可用。锁不是银弹，但对于缓存击穿、重复计算等典型问题，这套组件能显著提升代码的可维护性和系统的稳定性。
