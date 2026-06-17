package tutorials4j.framework.schedule.core.component;

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
import tutorials4j.framework.schedule.core.bean.TaskRuntimeData;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class RunnableDecorator implements Runnable, Trigger {
  private static final AtomicInteger NO = new AtomicInteger(0);
  @Getter private final Task task;
  private final TaskRunner runner;
  private CronTrigger cronTrigger;

  @Getter private TaskRuntimeData lastTaskRuntimeData;

  private Consumer<TaskRuntimeData> startEvent;
  private Consumer<TaskRuntimeData> completeEvent;

  private Consumer<TaskRuntimeData> stopEvent;

  private Consumer<TaskRuntimeData> failureEvent;

  private final String lotNo;

  private final AtomicInteger totalCount = new AtomicInteger(0);

  private final AtomicInteger totalFailureCount = new AtomicInteger(0);

  public RunnableDecorator(Task task, TaskRunner runner) {
    this.task = task;
    this.runner = runner;
    this.cronTrigger = new CronTrigger(task.getCron());
    this.lotNo = String.format("%06d - %s", NO.incrementAndGet(), Instant.now());
  }

  @Override
  public Instant nextExecution(TriggerContext triggerContext) {
    Integer maxExecutionCount = task.getMaxExecutionCount();
    if (maxExecutionCount != null && maxExecutionCount <= totalCount.get()) {
      if (stopEvent != null) {
        String message = String.format("已最大超过最大执行数量，将停止任务，最大执行数量=%s", maxExecutionCount);
        stopEvent.accept(buildTaskRuntimeData().message(message).build());
      }
      return null;
    }

    Integer maxFailureCount = task.getMaxFailureCount();
    if (maxFailureCount != null && maxFailureCount <= totalFailureCount.get()) {
      if (stopEvent != null) {
        String message = String.format("已最大超过最大失败数量，将停止任务，最大执行数量=%s", maxFailureCount);
        stopEvent.accept(buildTaskRuntimeData().message(message).build());
      }
      return null;
    }

    if (!Objects.equals(cronTrigger.getExpression(), task.getCron())) {
      cronTrigger = new CronTrigger(task.getCron());
    }
    Instant nextExecutionTime = cronTrigger.nextExecution(triggerContext);

    Instant dueDate = task.getDueDate();
    if (dueDate != null && dueDate.isBefore(nextExecutionTime)) {
      if (stopEvent != null) {
        String message = String.format("已超过任务结束日期，将停止任务，任务结束日期=%s", dueDate);
        stopEvent.accept(buildTaskRuntimeData().message(message).build());
      }
      return null;
    }

    if (totalCount.get() == 0 && task.getInitialDelay() != null) {
      // 第一次执行, 添加延时
      return nextExecutionTime.plusMillis(task.getInitialDelay().toMillis());
    } else {
      return nextExecutionTime;
    }
  }
  ;

  @Override
  public void run() {
    TaskRuntimeData.TaskRuntimeDataBuilder taskRuntimeDataBuilder = buildTaskRuntimeData();
    if (startEvent != null) {
      startEvent.accept(taskRuntimeDataBuilder.startTime(Instant.now()).build());
    }

    try {
      runner.run(task.getMetadata());
      if (completeEvent != null) {
        completeEvent.accept(taskRuntimeDataBuilder.endTime(Instant.now()).build());
      }
    } catch (Throwable t) {
      if (failureEvent != null) {
        failureEvent.accept(taskRuntimeDataBuilder.throwable(t).build());
      }
      throw t;
    }
  }

  public RunnableDecorator onStart(Consumer<TaskRuntimeData> consumer) {
    this.startEvent =
        data -> {
          totalCount.incrementAndGet();
          lastTaskRuntimeData = data;
          consumer.accept(data);
        };
    return this;
  }

  public RunnableDecorator onStop(Consumer<TaskRuntimeData> consumer) {
    this.stopEvent =
        data -> {
          lastTaskRuntimeData = data;
          consumer.accept(data);
        };
    return this;
  }

  public RunnableDecorator onComplete(Consumer<TaskRuntimeData> consumer) {
    this.completeEvent =
        data -> {
          lastTaskRuntimeData = data;
          consumer.accept(data);
        };
    return this;
  }

  public RunnableDecorator onFailure(Consumer<TaskRuntimeData> consumer) {
    this.failureEvent =
        data -> {
          totalFailureCount.incrementAndGet();
          lastTaskRuntimeData = data;
          consumer.accept(data);
        };
    return this;
  }

  public TaskRuntimeData.TaskRuntimeDataBuilder buildTaskRuntimeData() {
    return TaskRuntimeData.builder()
        .taskCode(task.getTaskCode())
        .lotNo(lotNo)
        .totalCount(totalCount.get())
        .totalFailureCount(totalFailureCount.get())
        .timestamp(Instant.now());
  }
}
