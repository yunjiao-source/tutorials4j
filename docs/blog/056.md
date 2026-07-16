# [056][调度模块]RunnableDecorator – 任务执行的增强装饰器

本项目代码: https://gitee.com/yunjiao-source/tutorials4j


## 摘要
调度框架不仅要“按时执行”，还需支持**执行次数限制、失败重试上限、初始延迟、截止日期**等高级特性。本文分析 `RunnableDecorator` 如何同时实现 `Runnable` 和 `Trigger` 接口，统一封装执行控制与下一次执行时间计算。

## 1. 双重职责：执行逻辑 + 触发逻辑
```java
public class RunnableDecorator implements Runnable, Trigger {
    private final Task task;
    private final TaskRunner runner;
    private CronTrigger cronTrigger;
    // 统计字段与回调函数...
}
```
- 作为 `Runnable`：在 `run()` 中执行真正的业务逻辑，并记录开始/完成/失败事件。
- 作为 `Trigger`：在 `nextExecution(TriggerContext)` 中根据 cron、最大执行次数、最大失败次数、截止日期等决定是否继续调度。

## 2. 丰富的执行控制策略
```java
@Override
public Instant nextExecution(TriggerContext triggerContext) {
    // 超过最大执行次数
    if (maxExecutionCount != null && maxExecutionCount <= totalCount.get()) {
        stopEvent.accept(...);
        return null;
    }
    // 超过最大失败次数
    if (maxFailureCount != null && maxFailureCount <= totalFailureCount.get()) {
        stopEvent.accept(...);
        return null;
    }
    // 超过截止日期
    Instant dueDate = task.getDueDate();
    if (dueDate != null && dueDate.isBefore(next)) {
        stopEvent.accept(...);
        return null;
    }
    // 第一次执行增加 initialDelay
    if (totalCount.get() == 0) {
        return next.plusMillis(task.getInitialDelay().toMillis());
    }
    return next;
}
```
- **失败计数**：每次任务抛出异常时，`failureEvent` 回调自增 `totalFailureCount`。
- **执行次数**：每次 `run()` 开始前，`startEvent` 自增 `totalCount`。
- **初始延迟**：首次执行时在 cron 计算结果上额外延迟，避免任务一启动就立即运行。

## 3. 执行历史记录
```java
private final Queue<TaskCondition> taskConditionHistory = EvictingQueue.create(30);
```
- 使用 Guava 的 `EvictingQueue` 保留最近 30 次执行记录，可用于监控、审计或自动重试决策。
- 每次执行完成后生成 `TaskCondition`（包含起止时间、错误信息等）并存入队列。

## 4. 设计价值
将控制逻辑与业务逻辑彻底分离，使得开发者只需关注 `doRun()` 实现，而执行策略（限流、延迟、截止）由框架统一管理，符合“开闭原则”。