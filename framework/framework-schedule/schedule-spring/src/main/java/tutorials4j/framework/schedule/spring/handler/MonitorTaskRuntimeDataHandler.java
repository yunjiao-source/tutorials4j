package tutorials4j.framework.schedule.spring.handler;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;

/**
 * 将任务运行数据上报为 Micrometer 指标的任务处理器。
 *
 * <p>根据任务的运行状态（创建、启动、完成、停止、取消、异常）记录对应的计数器， 并在任务完成时通过 Timer 与 Gauge 统计执行耗时分布与最小时长。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class MonitorTaskRuntimeDataHandler implements TaskRuntimeDataHandler {
  /** Micrometer 指标注册中心。 */
  private final MeterRegistry meterRegistry;

  /** 任务编码到最小时长跟踪器的缓存。 */
  private final ConcurrentMap<String, AtomicLong> minDurationCache = new ConcurrentHashMap<>();

  /**
   * 异步处理任务运行数据：按状态记录计数指标与耗时指标。
   *
   * @param data 任务运行数据
   */
  @Async
  @Override
  public void handle(TaskRuntimeData data) {

    if (Objects.equals(data.taskStatus(), TaskStatusEnum.CREATED)) {
      Counter.builder("schedule.task.created")
          .tag("status", data.taskStatus().name())
          .tag("taskCode", data.taskCode())
          .description("计划任务创建次数")
          .register(meterRegistry)
          .increment();
    }

    if (Objects.equals(data.taskStatus(), TaskStatusEnum.STARTED)) {
      Counter.builder("schedule.task.started")
          .tag("status", data.taskStatus().name())
          .tag("taskCode", data.taskCode())
          .description("计划任务启动次数")
          .register(meterRegistry)
          .increment();
    }

    if (Objects.equals(data.taskStatus(), TaskStatusEnum.COMPLETED)) {
      String taskCode = data.taskCode();

      Counter.builder("schedule.task.completed")
          .tag("status", data.taskStatus().name())
          .tag("taskCode", taskCode)
          .description("计划任务完成次数")
          .register(meterRegistry)
          .increment();

      // 计算单次运行时长（毫秒）
      long durationMillis = Duration.between(data.startTime(), data.endTime()).toMillis();

      // ========== 总时长 & 最大时长（利用 Timer 自动聚合） ==========
      Timer timer =
          Timer.builder("schedule.task.duration")
              .tag("taskCode", taskCode)
              .description("任务执行耗时分布（毫秒）")
              .register(meterRegistry);
      timer.record(Duration.ofMillis(durationMillis));

      // computeIfAbsent 保证每个 taskCode 只初始化一次
      AtomicLong minTracker =
          minDurationCache.computeIfAbsent(
              taskCode,
              key -> {
                AtomicLong initialValue = new AtomicLong(Long.MAX_VALUE);
                // 注册 Gauge，暴露当前最小值
                Gauge.builder("schedule.task.duration.min", initialValue, AtomicLong::longValue)
                    .tag("taskCode", taskCode)
                    .description("任务执行最小时长（毫秒）")
                    .register(meterRegistry);
                return initialValue;
              });
      // 原子更新：取当前值和本次值的最小值
      minTracker.updateAndGet(current -> Math.min(current, durationMillis));
    }

    if (Objects.equals(data.taskStatus(), TaskStatusEnum.STOPPED)) {
      Counter.builder("schedule.task.stopped")
          .tag("status", data.taskStatus().name())
          .tag("taskCode", data.taskCode())
          .description("计划任务停止次数")
          .register(meterRegistry)
          .increment();
    }

    if (Objects.equals(data.taskStatus(), TaskStatusEnum.CANCELLED)) {
      Counter.builder("schedule.task.canceled")
          .tag("status", data.taskStatus().name())
          .tag("taskCode", data.taskCode())
          .description("计划任务取消次数")
          .register(meterRegistry)
          .increment();
    }

    if (Objects.equals(data.taskStatus(), TaskStatusEnum.EXCEPTION)) {
      Counter.builder("schedule.task.exception")
          .tag("status", data.taskStatus().name())
          .tag("taskCode", data.taskCode())
          .description("计划任务异常次数")
          .register(meterRegistry)
          .increment();
    }
  }
}
