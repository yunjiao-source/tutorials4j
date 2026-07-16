# [073][示例]基于Redis分布式锁的定时任务调度实践

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在微服务或集群环境中，定时任务往往会同时运行在多个节点上。如果任务涉及共享资源（如数据库更新、文件操作、消息消费），就必须确保**同一时刻只有一个节点执行**，否则会导致数据错乱、重复处理甚至系统崩溃。本文基于一套内部框架（`tutorials4j`）的示例代码，剖析如何利用 **Redis 分布式锁** 配合 **Spring Schedule** 实现安全、可靠的定时任务调度，并深入比较两种锁模式——**固定租期（Fixed Lease）** 与**自动续期（Auto Renewal）** 的适用场景与潜在风险。

---

## 1. 整体架构与核心组件

示例涉及以下关键类：

- `RedisLockService`：Redis 分布式锁核心服务，提供 `FixedLease` 和 `AutoRenewal` 两种内部类，分别对应两种锁模式。
- `FixedLeaseLockTaskRunner` 与 `AutoRenewalLockTaskRunner`：两个接口，封装了获取锁、执行业务、释放锁的模板流程。业务方只需实现 `key()`、`doRun()` 和 `handleException()`。
- `Demo1AutoRenewalTaskRunner` 与 `Demo2FixedLeaseTaskRunner`：具体业务实现，模拟随机耗时任务。
- `ScheduleConfig`：利用 `@Scheduled` 触发任务，每个任务配置了两个方法，故意制造锁竞争，用于验证锁的互斥性。
- `application-redis.yml`：配置 Spring 调度线程池和自动续期专用线程池。

---

## 2. 两种锁模式详解

### 2.1 固定租期（FixedLease）

**原理**：加锁时指定一个固定的过期时间（如 3 秒），到期后 Redis 自动删除 Key，锁即释放。业务必须在租期内完成，否则锁会被“抢走”，导致其他节点获得锁并执行。

**核心代码**（`FixedLease.lock`）：
```java
String value = redisTemplate.execute(
    SCRIPT_LOCK,
    Collections.singletonList(lockKey),
    lockId,
    String.valueOf(expireTime.toMillis())
);
```
使用 Lua 脚本 `SET key value NX PX milliseconds` 保证原子性。

**特点**：
- 实现简单，没有额外线程开销。
- 适用于**执行时间明确且较短**的任务（如秒级操作）。
- 风险：若任务耗时超过租期，锁会提前失效，造成**多个节点同时执行**（即锁失效导致并发），且当前节点完成时尝试释放锁（通过比较 lockId）不会误删，但业务已处于无锁保护状态。

**示例中的体现**：`Demo2FixedLeaseTaskRunner` 设置租期 3 秒，而业务随机睡眠 0~10 秒，因此大量任务会超时，日志会频繁出现锁获取失败或任务重叠。

---

### 2.2 自动续期（AutoRenewal）

**原理**：加锁时设置一个默认过期时间（30 秒），然后启动一个**定时续期线程**，每隔 9 秒（租期的 1/3）执行一次 Lua 脚本 `PEXPIRE` 重置过期时间。只要持有锁的线程还在运行，续期线程就会不断“续命”，确保锁在整个业务执行期间有效。业务结束后，主动取消续期任务并释放锁。

**核心代码**（`AutoRenewal.renewalLockTask`）：
```java
ScheduledFuture<?> future = executorServiceHolder
    .instance()
    .scheduleAtFixedRate(
        () -> {
            Boolean renewed = redisTemplate.execute(
                SCRIPT_RENEWAL,
                Collections.singletonList(lockKey),
                lockId,
                String.valueOf(DEFAULT_EXPIRE_TIME.toMillis())
            );
            if (!Boolean.TRUE.equals(renewed)) {
                cancelLockTask(lockKey); // 续期失败则停止
            }
        },
        DEFAULT_RENEWAL_PERIOD_TIME.toMillis(),
        DEFAULT_RENEWAL_PERIOD_TIME.toMillis(),
        TimeUnit.MILLISECONDS
    );
renewalTasks.put(lockKey, future);
```

**特点**：
- 适用于**执行时间不确定或较长**的任务（如批处理、数据同步）。
- 有效避免了锁过期引发的并发问题。
- 代价：需要额外的线程资源管理，且续期线程本身可能因网络问题续期失败，此时锁会过期，任务应具备容错能力。

**示例中的体现**：`Demo1AutoRenewalTaskRunner` 睡眠 0~10 秒，远小于 30 秒租期，续期机制保证锁始终有效，因此两个同时触发的 `demo1` 和 `demo1_1` 只有一个能成功获取锁，另一个抛出锁获取异常并被 `handleException` 记录。

---

## 3. 任务执行流程与异常处理

两个 `TaskRunner` 接口的 `run` 方法采用了**模板方法模式**：

```java
@Override
default void run(Map<String, String> params) {
    try {
        RedisLockService.instance.fixedLease().doInLock(key(), expireTime(), () -> doRun(params));
    } catch (BaseRuntimeException e) {
        if (e.getErrorCode() instanceof CacheErrorCode) {
            handleException(e);
        }
        throw e;
    }
}
```
- 内部调用 `doInLock`，它会尝试加锁，成功则执行 `doRun`，最终在 `finally` 中解锁。
- 若加锁失败（锁已被占用），`doInLock` 会抛出 `CacheErrorCode.CACHE_ACCQUIRE_LOCK_FAILURE` 异常，该异常被捕获后，调用 `handleException` 记录错误日志，然后继续向外抛出（避免影响调度器本身）。
- 开发者只需实现 `doRun` 和 `handleException`，无需关心锁的细节。

---

## 4. 调度配置与线程池分析

`ScheduleConfig` 中配置了四个定时方法：

```java
@Scheduled(initialDelay = 3000, fixedDelay = 5000)
public void deom1() { demo1.run(null); }

@Scheduled(initialDelay = 3000, fixedDelay = 5000)
public void deom1_1() { demo1.run(null); }

@Scheduled(initialDelay = 4000, fixedDelay = 3000)
public void deom2() { demo2.run(null); }

@Scheduled(initialDelay = 4000, fixedDelay = 3000)
public void deom2_1() { demo2.run(null); }
```

- `demo1` 与 `demo1_1` 共享同一个锁 Key（`schedule:demo1`），同时触发时只有一个能获得锁，另一个会立即失败（无等待机制）。
- `demo2` 与 `demo2_1` 同理，但租期只有 3 秒，若任务执行超过 3 秒，锁释放后另一个调度可能紧接着获取到锁，造成重叠执行。

**线程池配置**：
- `spring.task.scheduling.pool.size=3`：Spring 调度器线程池大小为 3，足以同时运行多个定时方法。
- `tutorials4j.cache.lock.redis.auto-renewal.core-pool-size=2`：自动续期专用线程池大小为 2，用来执行续期任务。这意味着同一时刻最多只有 2 个续期线程在运行，若并发续期任务数超过 2，会排队等待。

**关键注意点**：自动续期线程池大小需根据并发锁的数量合理配置，否则可能导致续期延迟，甚至锁提前过期。

---

## 5. 实践建议与常见陷阱

### 5.1 锁 Key 的设计
- 使用有意义的前缀（如 `schedule:demo1`），便于监控和排查。
- 避免 Key 过长，节省内存。

### 5.2 固定租期模式的使用原则
- **必须确保任务执行时间 < 租期的 80%**，预留缓冲，防止因系统抖动导致超时。
- 若任务时间不可控，应优先选择自动续期模式。

### 5.3 自动续期模式的注意事项
- 业务逻辑中应避免死循环或无限阻塞，否则续期线程会一直运行，造成资源浪费。
- 续期线程本身是守护性质的，若主线程异常退出（如 `doRun` 抛出未捕获异常），`finally` 中会取消续期并释放锁，防止死锁。
- 若 Redis 网络闪断导致续期失败，锁会过期，此时其他节点可能抢占锁，原任务仍在运行（因为业务线程并未感知），可能造成数据冲突。业务应设计为**幂等**或具备**状态检测**能力。

### 5.4 异常处理策略
- 示例中 `handleException` 仅记录日志，实际生产可结合告警系统（如发送邮件、钉钉消息）及时通知运维。
- 对于锁获取失败，通常无需重试（因为下一次调度很快到来），除非业务要求立即重试。

### 5.5 线程池隔离
- 调度线程池与续期线程池分离，避免续期任务阻塞调度线程。
- 续期线程池的大小建议根据最大并发锁数量设定，经验值可为并发数 * 1.5。

---

## 6. 总结

通过本次示例代码的分析，我们看到了一个基于 Redis 的分布式锁在定时任务调度中的完整实现。**固定租期**简单高效，适合短平快任务；**自动续期**则通过后台续期线程保障长任务的锁安全。二者结合使用，能够覆盖绝大多数分布式调度场景。

在实际落地时，我们还需考虑：
- **锁粒度**：是任务级锁还是数据级锁？示例中为任务级，即整个任务互斥。
- **监控**：增加锁获取失败次数、续期成功率等指标，便于排查问题。
- **降级**：当 Redis 不可用时，是否有本地锁或熔断策略。

分布式锁并非银弹，它引入了网络依赖和性能开销，但合理使用能极大提升系统稳定性。希望本文能为你在设计分布式任务调度时提供有益的参考。