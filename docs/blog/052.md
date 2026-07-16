# [052][核心模块]Java线程池封装实践：`ExecutorServiceHolder` 设计与实现

本项目代码: https://gitee.com/yunjiao-source/tutorials4j

在 Java 后端开发中，线程池的创建、配置和优雅关闭是一个常见但容易出错的环节。本文介绍一个轻量级的封装工具 —— `ExecutorServiceHolder`，它结合 `ExecutionOption` 配置类，帮助开发者规范、安全地管理 `ThreadPoolExecutor` 和 `ScheduledThreadPoolExecutor`。

## 一、整体设计概览

`ExecutorServiceHolder` 是一个泛型容器类，持有一个 `ExecutorService` 实例及其对应的配置选项 `ExecutionOption`。它提供了两个静态工厂方法：
- `buildScheduler(ExecutionOption)` → 创建 `ScheduledThreadPoolExecutor`
- `buildThreadPool(ExecutionOption)` → 创建 `ThreadPoolExecutor`

此外，`shutdown()` 方法实现了带超时等待的优雅关闭逻辑。  
配套的 `ExecutionOption` 类采用 Builder 风格（实际为 JavaBean + 默认值），允许开发者以声明式方式配置线程池参数。

## 二、核心类解析

### 1. `ExecutionOption` – 线程池配置清单

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `corePoolSize` | 核心线程数 | 1 |
| `maximumPoolSize` | 最大线程数 | 3 |
| `threadNamePrefix` | 线程名前缀 | `"t4j-thread-pool-"` |
| `daemon` | 是否为守护线程 | `false` |
| `allowCoreThreadTimeOut` | 是否允许核心线程空闲超时回收 | `false` |
| `keepAlive` | 空闲线程存活时间 | `Duration.ofSeconds(60)` |
| `queueCapacity` | 任务队列容量 | 100 |
| `rejectedPolicy` | 拒绝策略枚举 | `ABORT` |
| `awaitTermination` | 关闭时是否等待任务完成 | `true` |
| `awaitTerminationPeriod` | 等待超时时间 | `Duration.ofSeconds(30)` |

**拒绝策略枚举** 对应标准 `RejectedExecutionHandler`：
- `ABORT` → `AbortPolicy`（抛异常）
- `CALLER_RUNS` → `CallerRunsPolicy`（调用者线程执行）
- `DISCARD` → `DiscardPolicy`（静默丢弃）
- `DISCARD_OLDEST` → `DiscardOldestPolicy`（丢弃队首，重试当前）

`getRejectedExecutionHandler()` 方法根据枚举返回对应的处理器实例。

### 2. `ExecutorServiceHolder` – 线程池持有器

#### 工厂方法细节

**创建 `ScheduledThreadPoolExecutor`**：
```java
ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
    option.getCorePoolSize(), threadFactory, option.getRejectedExecutionHandler());
executor.setRemoveOnCancelPolicy(true);  // 取消任务时立即从队列移除
```

**创建 `ThreadPoolExecutor`**：
```java
new ThreadPoolExecutor(
    corePoolSize, maximumPoolSize,
    keepAlive.toMillis(), TimeUnit.MILLISECONDS,
    new ArrayBlockingQueue<>(queueCapacity),
    threadFactory,
    rejectedHandler
);
```
- 队列固定为 `ArrayBlockingQueue`（有界队列），避免无界队列导致内存溢出。
- 若 `allowCoreThreadTimeOut == true`，调用 `executor.allowCoreThreadTimeOut(true)` 使核心线程也能被回收。

#### 线程工厂 – `NamedThreadFactory`

内部静态类，实现 `ThreadFactory`，为每个线程池和线程分配唯一 ID：
- 全局静态计数器 `THREAD_POOL_ID` 记录当前池序号（从1开始）
- 线程名格式：`{prefix}{poolId}-{threadNumber}`  
  例如：`t4j-thread-pool-2-5`
- 支持设置 `daemon` 属性

#### 优雅关闭方法 `shutdown()`

```java
public void shutdown() {
    if (option.isAwaitTermination()) {
        instance.shutdown();                       // 拒绝新任务
        if (!instance.awaitTermination(timeout, unit)) {
            log.warn("Timeout, forcing shutdown...");
            instance.shutdownNow();                // 超时强制终止
        }
    } else {
        instance.shutdownNow();                    // 立即强制关闭
    }
}
```
- 当 `awaitTermination = true` 时，先调用 `shutdown()`，然后等待指定时长；若超时则调用 `shutdownNow()`。
- 若等待期间发生 `InterruptedException`，同样执行 `shutdownNow()` 并恢复中断标志。
- 该设计既保证了尽可能完成任务，又避免了无限期阻塞。

## 三、使用示例

### 1. 创建并启动一个通用线程池

```java
ExecutionOption option = new ExecutionOption();
option.setCorePoolSize(4);
option.setMaximumPoolSize(8);
option.setThreadNamePrefix("biz-");
option.setQueueCapacity(200);
option.setRejectedPolicy(ExecutionOption.RejectedPolicy.CALLER_RUNS);
option.setAwaitTermination(true);
option.setAwaitTerminationPeriod(Duration.ofSeconds(10));

ExecutorServiceHolder<ThreadPoolExecutor> holder = 
    ExecutorServiceHolder.buildThreadPool(option);

ThreadPoolExecutor pool = holder.instance();
pool.submit(() -> System.out.println("task running"));
```

### 2. 创建调度线程池

```java
ExecutionOption schedulerOption = new ExecutionOption();
schedulerOption.setCorePoolSize(2);
schedulerOption.setDaemon(true);          // 守护线程，不阻止JVM退出
schedulerOption.setThreadNamePrefix("scheduler-");

ExecutorServiceHolder<ScheduledThreadPoolExecutor> schedulerHolder = 
    ExecutorServiceHolder.buildScheduler(schedulerOption);

ScheduledThreadPoolExecutor scheduler = schedulerHolder.instance();
scheduler.scheduleAtFixedRate(() -> log.info("heartbeat"), 0, 5, TimeUnit.SECONDS);
```

### 3. 应用关闭时释放资源

```java
// 在 Spring @PreDestroy 或 应用退出钩子中调用
schedulerHolder.shutdown();
threadPoolHolder.shutdown();
```

## 四、设计亮点与注意事项

### ✅ 亮点

1. **类型安全**：`ExecutorServiceHolder<T extends ExecutorService>` 保留具体线程池类型，便于调用特有方法（如 `scheduleAtFixedRate`）。
2. **统一配置模型**：所有线程池参数通过 `ExecutionOption` 管理，避免散落在代码各处。
3. **优雅关闭标准化**：封装了 `shutdown` + `awaitTermination` 的常见模式，减少遗漏。
4. **可读性强的线程命名**：池ID + 线程序号，便于定位问题。
5. **有界队列 + 拒绝策略组合**：防止无界队列引发 OOM，且拒绝策略可灵活切换。

### ⚠️ 注意事项

- `queueCapacity` 只用于 `ThreadPoolExecutor`，`ScheduledThreadPoolExecutor` 使用无界延迟队列，需注意任务积压风险。
- `keepAlive` 对核心线程生效的前提是设置了 `allowCoreThreadTimeOut = true`。
- 调用 `shutdown()` 后，`holder` 仍然持有已关闭的线程池实例；不应再提交任务。
- 当前实现未提供动态调整参数的能力，如需调整需自行扩展。

## 五、总结

`ExecutorServiceHolder` 配合 `ExecutionOption` 提供了一种简洁、安全、可配置的线程池管理方案。它将创建参数、线程命名、优雅关闭等关注点集中处理，减少了样板代码，提升了系统健壮性。适用于需要多线程池隔离、需要规范关闭流程的中大型 Java 应用，可作为基础工具类集成到框架中。

**扩展建议**：后续可增加对 `ForkJoinPool` 的支持、添加 JMX 监控暴露线程池指标、支持 Spring 生命周期自动注册等。