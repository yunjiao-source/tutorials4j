# [074][示例]基于Redisson的分布式锁在定时任务中的实践与异常模拟

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在分布式系统中，定时任务往往部署在多台服务器上，若不加控制，同一任务可能被多个节点同时执行，引发数据重复、资源竞争甚至系统崩溃。**分布式锁**是解决这一问题的标准方案。本文围绕一个基于 **Redisson** 的分布式锁框架，通过四个具体示例，深入剖析**自动续期锁**与**固定租期锁**、**阻塞锁**与**可重入锁**的差异，并展示如何通过定时任务模拟锁竞争、超时等异常场景，帮助读者在实际项目中做出正确的选型和调优。

---

## 一、框架整体设计

框架提供四个接口，均继承自 `TaskRunner` 和 `Lockable`，分别对应四种锁模式：

| 接口 | 锁类型 | 租期策略 | 可重入 | 等待时间 |
|------|--------|----------|--------|----------|
| `AutoRenewalBlockLockTaskRunner` | 阻塞 | 自动续期 | 否 | 无（立即失败） |
| `AutoRenewalReentrantLockTaskRunner` | 可重入 | 自动续期 | 是 | 可配置（默认1s） |
| `FixedLeaseBlockLockTaskRunner` | 阻塞 | 固定租期 | 否 | 无 |
| `FixedLeaseReentrantLockTaskRunner` | 可重入 | 固定租期 | 是 | 可配置 |

每个接口的 `run()` 方法由框架实现，内部调用 `RedissonBlockLockService` 或 `RedissonReentrantLockService` 执行加锁逻辑，并将业务逻辑委托给子类的 `doRun()`。异常统一由 `handleException()` 处理，但仅捕获 `CacheErrorCode` 类型异常，其他异常继续向上抛出，保证调度层能感知任务失败。

---

## 二、四种锁模式详解

### 1. 自动续期阻塞锁（`Demo1`）

```java
public class Demo1AutoRenewalBlockTaskRunner implements AutoRenewalBlockLockTaskRunner {
    @Override
    public String key() { return "auto-renewal-block:demo1"; }
    @Override
    public void doRun(Map<String, String> params) {
        // 模拟业务耗时 0~10 秒
        Thread.sleep(ThreadLocalRandom.current().nextInt(10000));
    }
}
```

- **特点**：基于 Redisson 的 **看门狗（Watchdog）** 机制，锁默认租期 30 秒，业务未完成则自动续期，直至任务结束。
- **适用场景**：**任务执行时间不可预估**，如数据清洗、报表生成等长耗时操作。
- **注意**：阻塞锁意味着获取锁失败时**立即抛出异常**，不等待。适合对实时性要求高、宁可失败也不愿排队等待的场景。

---

### 2. 自动续期可重入锁（`Demo2`）

```java
public class Demo2AutoRenewalReentrantTaskRunner implements AutoRenewalReentrantLockTaskRunner {
    @Override
    public Duration waitTime() { return Duration.ofSeconds(1); }
    // doRun 同样随机睡眠 0~10 秒
}
```

- **特点**：可重入锁允许同一线程多次获取锁；设置 `waitTime=1s`，获取锁失败时会等待最多 1 秒，超时抛出异常。
- **适用场景**：**任务内部存在嵌套锁调用**，或希望给锁获取一定的缓冲时间，减少因瞬时竞争导致的失败。
- **对比**：与阻塞锁相比，更“宽容”，适合对任务成功率有一定要求的场景。

---

### 3. 固定租期阻塞锁（`Demo3`）

```java
public class Demo3FixedLeaseBlockTaskRunner implements FixedLeaseBlockLockTaskRunner {
    @Override
    public Duration expireTime() { return Duration.ofSeconds(5); }
    // doRun 随机睡眠 0~10 秒
}
```

- **特点**：锁租期固定为 5 秒，**不会自动续期**。若业务执行超过 5 秒，锁会被 Redisson 自动释放，其他节点可获取锁，导致任务并发执行。
- **适用场景**：**任务执行时间严格可控**，且要求锁尽快释放，避免因异常导致锁长期占用。
- **风险**：必须确保 `expireTime` 大于任务最大执行时间，否则会出现锁提前释放的“假锁”问题。

---

### 4. 固定租期可重入锁（`Demo4`）

```java
public class Demo4FixedLeaseReentrantTaskRunner implements FixedLeaseReentrantLockTaskRunner {
    @Override
    public Duration waitTime() { return Duration.ofSeconds(1); }
    @Override
    public Duration expireTime() { return Duration.ofSeconds(5); }
}
```

- **特点**：结合固定租期与可重入，既限制锁存活时间，又支持重入。
- **注意**：框架调用为 `doInLock(key, waitTime(), waitTime(), ...)` —— 这里第二个参数是等待时间，第三个也是等待时间（可能是笔误，预期应为租期）。实际使用需核对框架源码，确保正确传入租期。

---

## 三、定时任务制造锁异常

`ScheduleConfig` 中为每个 Demo 配置了两个定时任务，初始延迟错开，固定延迟 6 秒，模拟多节点同时触发：

```java
@Scheduled(initialDelay = 3000, fixedDelay = 6000)
public void deom1() { demo1.run(null); }

@Scheduled(initialDelay = 5000, fixedDelay = 6000)
public void deom1_1() { demo1.run(null); }
```

- **竞争场景**：两个任务间隔 2 秒启动，后续每 6 秒重复。当任务耗时超过 2 秒时，第二次触发时前一次可能尚未释放锁，从而产生锁竞争。
- **超时场景**：`Demo3` 租期 5 秒，业务随机睡眠 0~10 秒，若睡眠超过 5 秒，锁自动失效，第二个任务即可获取锁，造成两个任务同时运行（日志中会出现两个 `>>>` 和 `<<<` 交叉）。

这些精心设计的触发时序，使得我们在日志中能清晰观察到：
- 获取锁成功/失败
- 锁自动续期（通过 Redisson 日志或监控）
- 锁提前释放导致的并发执行
- 可重入锁的等待超时

---

## 四、异常处理与日志

所有 Demo 均实现了 `handleException()`，仅记录错误日志：

```java
@Override
public void handleException(BaseRuntimeException exception) {
    log.error("{}: {}", key(), exception.getMessage());
}
```

框架在 `run()` 中捕获 `CacheErrorCode` 异常后回调该方法，然后 **继续抛出异常**，这样调度层（如 Spring `@Scheduled`）能感知任务失败，便于触发重试或告警。

**注意**：固定租期锁若因执行超时而导致锁被自动释放，业务线程并不感知，仍会继续执行，此时可能出现数据不一致。因此，使用固定租期锁时必须确保 `expireTime` 宽松，或配合业务逻辑中的“中断”机制（如通过 `Thread.interrupt()`）。

---

## 五、选型建议与实践要点

| 维度 | 自动续期锁 | 固定租期锁 |
|------|-----------|-----------|
| **执行时长** | 不确定或很长 | 确定且较短 |
| **锁安全性** | 高（不会提前释放） | 低（可能提前释放） |
| **资源占用** | 续期带来额外网络开销 | 无续期，轻量 |
| **异常处理** | 任务崩溃锁也会在租期后释放 | 需保证租期足够长 |

- **可重入性**：仅当业务逻辑中需要重复获取同一把锁时再选择可重入锁，否则使用非重入锁更轻量。
- **等待时间**：建议根据任务对延迟的容忍度设置，太短易失败，太长可能堆积请求。
- **监控与告警**：需监控锁获取失败率、持有时间、续期次数等指标，及时发现异常。

---

## 六、总结

本文通过一个具体的 Redisson 分布式锁框架，演示了四种锁模式在定时任务中的应用，并利用定时任务触发时序模拟了锁竞争、超时等真实场景。理解自动续期与固定租期的本质区别，结合业务执行时间的特征，是正确选型的关键。同时，异常处理的合理设计能帮助运维人员快速定位问题。

实际生产环境中，还可结合 **Spring 的 `@Async`** 或 **分布式调度平台（如 XXL-JOB）** 进行更精细的控制，但分布式锁依然是保障数据一致性的基石。希望本文能为您在微服务架构中的任务调度设计提供有价值的参考。