package tutorials4j.framework.schedule.spring.bean;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.Getter;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import tutorials4j.framework.schedule.core.bean.Task;
import tutorials4j.framework.schedule.core.bean.TaskRunner;
import tutorials4j.framework.schedule.spring.properties.TaskExecutionOptions;

/**
 * 任务运行装饰器。
 *
 * <p>同时实现 {@link Runnable} 与 {@link Trigger} 接口，将 {@link Task} 与 {@link TaskRunner} 包装为可由 Spring
 * 调度器执行的任务：根据 cron 表达式计算下次执行时间，并校验最大执行次数、 最大失败次数、任务结束日期与首次执行延时，同时统计执行数据并触发开始、完成、失败、停止等生命周期事件。
 *
 * @author Yun Jiao
 */
public class RunnableDecorator implements Runnable, Trigger {
  /** 批次号自增序号生成器。 */
  private static final AtomicInteger NO = new AtomicInteger(0);

  @Getter private final Task task;
  private final TaskExecutionOptions options;
  private final TaskRunner runner;
  private CronTrigger cronTrigger;

  private Consumer<TaskRuntimeData> startEvent;
  private Consumer<TaskRuntimeData> completeEvent;

  private Consumer<TaskRuntimeData> stopEvent;

  private Consumer<TaskRuntimeData> failureEvent;

  /** 本次运行的批次号，格式为"序号 - 时间"。 */
  private final String lotNo;

  /** 累计执行次数。 */
  private final AtomicInteger totalCount = new AtomicInteger(0);

  /** 累计失败次数。 */
  private final AtomicInteger totalFailureCount = new AtomicInteger(0);

  /**
   * 构造任务运行装饰器。
   *
   * @param task 待执行的任务定义
   * @param runner 任务执行器
   * @param options 任务执行选项，用于获取最大执行次数、最大失败次数等默认配置
   */
  public RunnableDecorator(Task task, TaskRunner runner, TaskExecutionOptions options) {
    this.task = task;
    this.runner = runner;
    this.options = options;
    this.cronTrigger = new CronTrigger(task.getCron());
    this.lotNo = String.format("%06d - %s", NO.incrementAndGet(), Instant.now());
  }

  /**
   * 计算任务的下一次执行时间。
   *
   * <p>依次校验最大执行次数、最大失败次数与任务结束日期，超出限制时触发停止事件并返回 {@code null}； 若 cron
   * 表达式发生变化则重建触发器；首次执行且配置了初始延时（initialDelay）时，在 cron 计算出的时间基础上追加延时。
   *
   * @param triggerContext 触发器上下文，用于计算基于 cron 的下次执行时间
   * @return 下次执行时间；若任务应停止则返回 {@code null}
   */
  @Override
  public Instant nextExecution(TriggerContext triggerContext) {
    Integer maxExecutionCount = task.getMaxExecutionCount();
    if (maxExecutionCount == null) {
      // 未定义获取配置属性值
      maxExecutionCount = options.getMaxExecutionCount();
    }
    if (maxExecutionCount != null && maxExecutionCount <= totalCount.get()) {
      if (stopEvent != null) {
        String message = String.format("已最大超过最大执行数量，将停止任务，最大执行数量=%s", maxExecutionCount);
        stopEvent.accept(
            buildTaskRuntimeData().taskStatus(TaskStatusEnum.STOPPED).message(message).build());
      }
      return null;
    }

    Integer maxFailureCount = task.getMaxFailureCount();
    if (maxFailureCount == null) {
      // 未定义获取配置属性值
      maxFailureCount = options.getMaxFailureCount();
    }
    if (maxFailureCount != null && maxFailureCount <= totalFailureCount.get()) {
      if (stopEvent != null) {
        String message = String.format("已最大超过最大失败数量，将停止任务，最大执行数量=%s", maxFailureCount);
        stopEvent.accept(
            buildTaskRuntimeData().taskStatus(TaskStatusEnum.STOPPED).message(message).build());
      }
      return null;
    }

    if (!Objects.equals(cronTrigger.getExpression(), task.getCron())) {
      cronTrigger = new CronTrigger(task.getCron());
    }
    Instant nextExecutionTime = cronTrigger.nextExecution(triggerContext);

    Instant dueDate = task.getDueDate();
    if (maxFailureCount == null) {
      // 未定义获取配置属性值
      dueDate = options.getDueDate();
    }
    if (dueDate != null && dueDate.isBefore(nextExecutionTime)) {
      if (stopEvent != null) {
        String message = String.format("已超过任务结束日期，将停止任务，任务结束日期=%s", dueDate);
        stopEvent.accept(
            buildTaskRuntimeData().taskStatus(TaskStatusEnum.STOPPED).message(message).build());
      }
      return null;
    }

    Duration initialDelay = task.getInitialDelay();
    if (initialDelay == null) {
      // 未定义获取配置属性值
      initialDelay = options.getInitialDelay();
    }
    if (totalCount.get() == 0 && initialDelay != null) {
      // 第一次执行, 添加延时
      return nextExecutionTime.plusMillis(initialDelay.toMillis());
    } else {
      return nextExecutionTime;
    }
  }
  ;

  /** 执行任务：触发开始事件，调用任务执行器执行业务逻辑，成功后触发完成事件，异常时触发失败事件并重新抛出异常。 */
  @Override
  public void run() {
    TaskRuntimeData.TaskRuntimeDataBuilder taskRuntimeDataBuilder = buildTaskRuntimeData();
    if (startEvent != null) {
      startEvent.accept(
          taskRuntimeDataBuilder
              .taskStatus(TaskStatusEnum.STARTED)
              .startTime(Instant.now())
              .build());
    }

    try {
      runner.run(task.getMetadata());
      if (completeEvent != null) {
        completeEvent.accept(
            taskRuntimeDataBuilder
                .taskStatus(TaskStatusEnum.COMPLETED)
                .endTime(Instant.now())
                .build());
      }
    } catch (Throwable t) {
      if (failureEvent != null) {
        failureEvent.accept(
            taskRuntimeDataBuilder.taskStatus(TaskStatusEnum.EXCEPTION).throwable(t).build());
      }
      throw t;
    }
  }

  /**
   * 注册任务开始事件回调，回调前会先累计总执行次数。
   *
   * @param consumer 开始事件消费者，接收任务运行数据
   * @return 当前装饰器实例，支持链式调用
   */
  public RunnableDecorator onStart(Consumer<TaskRuntimeData> consumer) {
    this.startEvent =
        data -> {
          totalCount.incrementAndGet();
          consumer.accept(data);
        };
    return this;
  }

  /**
   * 注册任务停止事件回调。
   *
   * @param consumer 停止事件消费者，接收任务运行数据
   * @return 当前装饰器实例，支持链式调用
   */
  public RunnableDecorator onStop(Consumer<TaskRuntimeData> consumer) {
    this.stopEvent = consumer;
    return this;
  }

  /**
   * 注册任务完成事件回调。
   *
   * @param consumer 完成事件消费者，接收任务运行数据
   * @return 当前装饰器实例，支持链式调用
   */
  public RunnableDecorator onComplete(Consumer<TaskRuntimeData> consumer) {
    this.completeEvent = consumer;
    return this;
  }

  /**
   * 注册任务失败事件回调，回调前会先累计总失败次数。
   *
   * @param consumer 失败事件消费者，接收任务运行数据
   * @return 当前装饰器实例，支持链式调用
   */
  public RunnableDecorator onFailure(Consumer<TaskRuntimeData> consumer) {
    this.failureEvent =
        data -> {
          totalFailureCount.incrementAndGet();
          consumer.accept(data);
        };
    return this;
  }

  /**
   * 构建任务运行数据构建器，包含任务编码、批次号、总执行次数、总失败次数与当前时间戳。
   *
   * @return 任务运行数据构建器
   */
  public TaskRuntimeData.TaskRuntimeDataBuilder buildTaskRuntimeData() {
    return TaskRuntimeData.builder()
        .taskCode(task.getTaskCode())
        .lotNo(lotNo)
        .totalCount(totalCount.get())
        .totalFailureCount(totalFailureCount.get())
        .timestamp(Instant.now());
  }
}
