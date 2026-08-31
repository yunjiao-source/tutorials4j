package tutorials4j.framework.oss.fss.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import tutorials4j.framework.oss.core.autoconfigure.MetricsOptions;

/**
 * 文件存储指标收集器。
 *
 * <p>负责创建和管理 Micrometer 指标（Counter、Timer、DistributionSummary、Gauge）， 提供成功、失败、活跃任务数、列表计数等记录方法。
 * 内部使用缓存避免重复注册 Meter。
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class FileStorageMetricsCollector {
  private final MeterRegistry registry;
  private final MetricsOptions options;

  // 缓存 Meter 实例，避免重复创建
  private final Map<String, Counter> successCounters = new ConcurrentHashMap<>();
  private final Map<String, Counter> failureCounters = new ConcurrentHashMap<>();
  private final Map<String, DistributionSummary> sizeSummaries = new ConcurrentHashMap<>();
  private final Map<String, Timer> timers = new ConcurrentHashMap<>();
  private final Map<String, AtomicLong> activeGauges = new ConcurrentHashMap<>();

  /**
   * 记录操作成功。
   *
   * @param operation 操作名称（如 upload, download）
   * @param platform 存储平台标识
   * @param extension 文件扩展名（可为 null）
   * @param size 文件大小（字节），<=0 时不记录大小
   * @param sample 计时样本（从 Timer.start 获取）
   */
  public void recordSuccess(
      String operation, String platform, String extension, long size, Timer.Sample sample) {
    if (!options.isEnabled()) return;

    String baseKey = operation + "." + platform;
    // 成功计数器
    Counter successCounter =
        successCounters.computeIfAbsent(
            baseKey + ".success",
            k ->
                Counter.builder("file.storage." + operation + ".success")
                    .tags(getCommonTags(platform, extension))
                    .tag("operation", operation)
                    .register(registry));
    successCounter.increment();

    // 文件大小（仅当 size>0）
    if (size > 0) {
      DistributionSummary sizeSummary =
          sizeSummaries.computeIfAbsent(
              baseKey + ".size",
              k ->
                  DistributionSummary.builder("file.storage." + operation + ".size")
                      .baseUnit("bytes")
                      .tags(getCommonTags(platform, extension))
                      .tag("operation", operation)
                      .publishPercentiles(options.getPercentiles())
                      .register(registry));
      sizeSummary.record(size);
    }

    // 耗时
    Timer timer =
        timers.computeIfAbsent(
            baseKey + ".duration",
            k ->
                Timer.builder("file.storage." + operation + ".duration")
                    .tags(getCommonTags(platform, extension))
                    .tag("operation", operation)
                    .publishPercentiles(options.getPercentiles())
                    .register(registry));
    sample.stop(timer);
  }

  /**
   * 记录操作失败。
   *
   * @param operation 操作名称
   * @param platform 存储平台
   * @param extension 文件扩展名
   * @param error 异常（用于提取错误类型）
   * @param sample 计时样本
   */
  public void recordFailure(
      String operation, String platform, String extension, Throwable error, Timer.Sample sample) {
    if (!options.isEnabled()) return;

    String errorType = error != null ? error.getClass().getSimpleName() : "unknown";
    String baseKey = operation + "." + platform + "." + errorType;

    Counter failureCounter =
        failureCounters.computeIfAbsent(
            baseKey + ".failure",
            k ->
                Counter.builder("file.storage." + operation + ".failure")
                    .tags(getCommonTags(platform, extension))
                    .tag("operation", operation)
                    .tag("error", errorType)
                    .register(registry));
    failureCounter.increment();

    // 失败也记录耗时
    Timer timer =
        timers.computeIfAbsent(
            operation + "." + platform + ".duration",
            k ->
                Timer.builder("file.storage." + operation + ".duration")
                    .tags(getCommonTags(platform, extension))
                    .tag("operation", operation)
                    .publishPercentiles(options.getPercentiles())
                    .register(registry));
    sample.stop(timer);
  }

  /** 活跃任务数 +1（Gauge） */
  public void activeTaskIncrement(String operation, String platform) {
    if (!options.isEnabled()) return;
    String key = operation + "." + platform;
    AtomicLong gauge =
        activeGauges.computeIfAbsent(
            key,
            k ->
                registry.gauge(
                    "file.storage." + operation + ".active",
                    Tags.of("platform", platform, "operation", operation),
                    new AtomicLong(0)));
    gauge.incrementAndGet();
  }

  /** 活跃任务数 -1 */
  public void activeTaskDecrement(String operation, String platform) {
    if (!options.isEnabled()) return;
    String key = operation + "." + platform;
    AtomicLong gauge = activeGauges.get(key);
    if (gauge != null) {
      gauge.decrementAndGet();
    }
  }

  /** 记录列举文件返回的数量 */
  public void recordListFilesCount(String platform, int count) {
    if (!options.isEnabled() || count <= 0) return;
    Counter counter =
        Counter.builder("file.storage.listFiles.count")
            .tags(getCommonTags(platform, null))
            .tag("operation", "listFiles")
            .register(registry);
    counter.increment(count);
  }

  /** 记录列举分片返回的分片数量 */
  public void recordListPartsCount(String platform, int count) {
    if (!options.isEnabled() || count <= 0) return;
    Counter counter =
        Counter.builder("file.storage.listParts.count")
            .tags(getCommonTags(platform, null))
            .tag("operation", "listParts")
            .register(registry);
    counter.increment(count);
  }

  /**
   * 生成通用标签（platform、extension、全局自定义标签）。
   *
   * @param platform 存储平台
   * @param extension 扩展名（可为 null）
   * @return 标签迭代器
   */
  private Iterable<Tag> getCommonTags(String platform, String extension) {
    Tags tags = Tags.of("platform", platform);
    if (StringUtils.hasText(extension)) {
      tags = tags.and("extension", extension);
    }
    // 额外全局标签（如有）
    for (String tag : options.getTags()) {
      String[] parts = tag.split("=", 2);
      if (parts.length == 2) {
        tags = tags.and(parts[0], parts[1]);
      }
    }
    return tags;
  }
}
