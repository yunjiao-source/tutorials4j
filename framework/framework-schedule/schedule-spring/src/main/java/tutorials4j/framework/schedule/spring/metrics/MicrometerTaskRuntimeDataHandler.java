package tutorials4j.framework.schedule.spring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import tutorials4j.framework.common.core.MetricsOptions;
import tutorials4j.framework.schedule.spring.bean.TaskRuntimeData;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;
import tutorials4j.framework.schedule.spring.handler.TaskRuntimeDataHandler;

/**
 * 基于 Micrometer 的任务运行数据处理器。
 *
 * <p>针对定时任务的生命周期事件，自动记录以下生产级指标：
 *
 * <ul>
 *   <li><b>task.status.count</b>（计数器）：按任务编码和状态统计事件发生次数
 *   <li><b>task.execution.duration</b>（计时器）：按任务编码和执行结果统计耗时，支持分位数
 *   <li><b>task.active.count</b>（仪表）：按任务编码统计当前正在运行的任务数量
 * </ul>
 *
 * 同时支持通过 {@link MetricsOptions} 控制指标启用、全局标签、百分位计算等。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class MicrometerTaskRuntimeDataHandler implements TaskRuntimeDataHandler {

  private final MeterRegistry meterRegistry;
  private final MetricsOptions options;

  // 用于记录每个任务当前活跃的任务数（STARTED 状态且尚未终止）
  private final Map<String, AtomicInteger> activeCounts = new ConcurrentHashMap<>();
  // 已注册 Gauge 的任务编码集合，避免重复注册
  private final Map<String, Boolean> registeredGauges = new ConcurrentHashMap<>();

  @Async
  @Override
  public void handle(TaskRuntimeData data) {
    TaskStatusEnum status = data.taskStatus();
    String taskCode = data.taskCode();

    // 构建基础标签：全局标签 + 任务编码
    Tags baseTags = Tags.of(getCommonTags()).and("taskCode", taskCode);

    // 1. 记录状态计数 (Counter)
    Counter.builder("task.status.count")
        .tags(baseTags.and("status", status.getName()))
        .register(meterRegistry)
        .increment();

    // 2. 维护活跃任务数 (Gauge)
    if (status == TaskStatusEnum.STARTED) {
      // 启动时增加活跃计数
      AtomicInteger counter = activeCounts.computeIfAbsent(taskCode, k -> new AtomicInteger(0));
      counter.incrementAndGet();
      // 首次遇到该任务时注册 Gauge
      registerGaugeIfNeeded(taskCode);
    } else if (isTerminalStatus(status)) {
      // 终止状态减少活跃计数
      AtomicInteger counter = activeCounts.get(taskCode);
      if (counter != null) {
        counter.decrementAndGet();
      } else {
        log.warn(
            "Received terminal event for taskCode={} without active counter, status={}",
            taskCode,
            status);
      }
    }

    // 3. 记录执行耗时 (Timer) - 仅针对有开始和结束时间的终止状态
    if (isTerminalStatus(status) && data.startTime() != null && data.endTime() != null) {
      Duration duration = Duration.between(data.startTime(), data.endTime());
      String result = mapResult(status);
      Tags timerTags = baseTags.and("result", result);

      Timer timer =
          Timer.builder("task.execution.duration")
              .tags(timerTags)
              .publishPercentiles(options.getPercentiles())
              .register(meterRegistry);
      timer.record(duration);
    }
  }

  /** 判断是否为终止状态（任务执行结束）。 */
  private boolean isTerminalStatus(TaskStatusEnum status) {
    return status == TaskStatusEnum.COMPLETED
        || status == TaskStatusEnum.EXCEPTION
        || status == TaskStatusEnum.STOPPED
        || status == TaskStatusEnum.CANCELLED;
  }

  /** 将终止状态映射为执行结果标签值。 */
  private String mapResult(TaskStatusEnum status) {
    return switch (status) {
      case COMPLETED -> "success";
      case EXCEPTION -> "failure";
      case STOPPED -> "stopped";
      case CANCELLED -> "cancelled";
      default -> "unknown";
    };
  }

  /** 按任务编码注册活跃任务数的 Gauge（只注册一次）。 */
  private void registerGaugeIfNeeded(String taskCode) {
    if (!registeredGauges.containsKey(taskCode)) {
      synchronized (this) {
        if (!registeredGauges.containsKey(taskCode)) {
          AtomicInteger counter = activeCounts.get(taskCode);
          if (counter != null) {
            Tags gaugeTags = Tags.of(getCommonTags()).and("taskCode", taskCode);
            Gauge.builder("task.active.count", counter, AtomicInteger::get)
                .tags(gaugeTags)
                .register(meterRegistry);
            registeredGauges.put(taskCode, Boolean.TRUE);
            log.debug("Registered Gauge for taskCode={}", taskCode);
          }
        }
      }
    }
  }

  /** 解析全局标签列表（格式：key=value）。 */
  private List<Tag> getCommonTags() {
    List<String> tagStrings = options.getTags();
    if (tagStrings == null || tagStrings.isEmpty()) {
      return List.of();
    }
    return tagStrings.stream()
        .map(s -> s.split("=", 2))
        .filter(arr -> arr.length == 2)
        .map(arr -> Tag.of(arr[0].trim(), arr[1].trim()))
        .collect(Collectors.toList());
  }
}
