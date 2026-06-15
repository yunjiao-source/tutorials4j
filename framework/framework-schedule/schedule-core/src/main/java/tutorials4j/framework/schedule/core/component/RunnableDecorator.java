package tutorials4j.framework.schedule.core.component;

import com.google.common.collect.EvictingQueue;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import tutorials4j.framework.schedule.core.bean.Task;
import tutorials4j.framework.schedule.core.bean.TaskRunData;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class RunnableDecorator implements Runnable, Trigger {
  private final Task task;
  private final TaskRunner runner;
  private CronTrigger cronTrigger;

  private Consumer<String> startEvent;
  private BiConsumer<String, TaskRunData> completeEvent;

  private BiConsumer<String, String> stopEvent;

  private BiConsumer<String, Throwable> failureEvent;

  private final Queue<TaskRunData> taskRunDataHistory = EvictingQueue.create(30);

  @Getter private TaskRunData lastTaskRunData;

  private final AtomicInteger totalCount = new AtomicInteger(0);

  private final AtomicInteger totalFailureCount = new AtomicInteger(0);

  public RunnableDecorator(Task task, TaskRunner runner) {
    this.task = task;
    this.runner = runner;
    this.cronTrigger = new CronTrigger(task.getCron());
  }

  @Nullable
  @Override
  public Instant nextExecution(TriggerContext triggerContext) {
    Integer maxExecutionCount = task.getMaxExecutionCount();
    if (maxExecutionCount != null && maxExecutionCount <= totalCount.get()) {
      if (stopEvent != null) {
        String message = String.format("已最大超过最大执行数量，将停止任务，最大执行数量=%s", maxExecutionCount);
        stopEvent.accept(task.getName(), message);
      }
      return null;
    }

    Integer maxFailureCount = task.getMaxFailureCount();
    if (maxFailureCount != null && maxFailureCount <= totalFailureCount.get()) {
      if (stopEvent != null) {
        String message = String.format("已最大超过最大失败数量，将停止任务，最大执行数量=%s", maxFailureCount);
        stopEvent.accept(task.getName(), message);
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
        stopEvent.accept(task.getName(), message);
      }
      return null;
    }

    if (totalCount.get() == 0) {
      // 第一次执行, 添加延时
      return nextExecutionTime.plusMillis(task.getInitialDelay().toMillis());
    } else {
      return nextExecutionTime;
    }
  }
  ;

  @Override
  public void run() {
    String name = task.getName();
    TaskRunData.TaskRunDataBuilder taskRunDataBuilder =
        TaskRunData.builder().timestamp(Instant.now());
    taskRunDataBuilder
        .startTime(Instant.now())
        .totalCount(totalCount.get())
        .totalFailureCount(totalFailureCount.get());
    if (startEvent != null) {
      startEvent.accept(name);
    }

    try {
      runner.run(task.getMetadata());
      lastTaskRunData = taskRunDataBuilder.endTime(Instant.now()).build();
      taskRunDataHistory.add(lastTaskRunData);

      if (completeEvent != null) {
        completeEvent.accept(name, lastTaskRunData);
      }
    } catch (Throwable t) {
      lastTaskRunData = taskRunDataBuilder.error(t.getMessage()).build();
      taskRunDataHistory.add(lastTaskRunData);

      if (failureEvent != null) {
        failureEvent.accept(name, t);
      }
      throw t;
    }
  }

  public RunnableDecorator onStart(Consumer<String> consumer) {
    this.startEvent =
        s -> {
          totalCount.incrementAndGet();
          consumer.accept(s);
        };
    return this;
  }

  public RunnableDecorator onStop(BiConsumer<String, String> consumer) {
    this.stopEvent = consumer;
    return this;
  }

  public RunnableDecorator onComplete(BiConsumer<String, TaskRunData> consumer) {
    this.completeEvent = consumer;
    return this;
  }

  public RunnableDecorator onFailure(BiConsumer<String, Throwable> consumer) {
    this.failureEvent =
        (s, t) -> {
          totalFailureCount.incrementAndGet();
          consumer.accept(s, t);
        };
    return this;
  }

  public List<TaskRunData> getTaskStatus() {
    return new ArrayList<>(taskRunDataHistory);
  }
}
