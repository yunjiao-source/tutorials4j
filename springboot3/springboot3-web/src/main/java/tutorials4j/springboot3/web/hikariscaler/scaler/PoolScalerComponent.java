package tutorials4j.springboot3.web.hikariscaler.scaler;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 连接池扩缩容器
 *
 * @author Yun Jiao
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PoolScalerComponent {
  private final HikariDataSource dataSource;

  private final DynamicPoolProperties properties;

  private final AtomicLong lastScaleTime = new AtomicLong(0);
  private final AtomicInteger consecutiveLowUsage = new AtomicInteger(0);

  public ScaleDecision evaluate(PoolStatistics current, List<PoolStatistics> history) {
    // 检查冷却时间
    long cooldownMillis = properties.getScaleUp().getCooldownTime().toMillis();
    if (System.currentTimeMillis() - lastScaleTime.get() < cooldownMillis) {
      return ScaleDecision.noScale("冷却期内，跳过评估");
    }

    // 评估扩容
    ScaleDecision scaleUpDecision = evaluateScaleUp(current, history);
    if (scaleUpDecision.isShouldScale()) {
      return scaleUpDecision;
    }

    // 评估缩容
    return evaluateScaleDown(current, history);
  }

  private ScaleDecision evaluateScaleUp(PoolStatistics current, List<PoolStatistics> history) {
    if (!properties.getScaleUp().isEnabled()) {
      return ScaleDecision.noScale("扩容已禁用");
    }

    int currentMax = current.getMaxPoolSize();
    int absoluteMax = properties.getScaleUp().getMaxPoolSize();

    // 已达到上限
    if (currentMax >= absoluteMax) {
      return ScaleDecision.noScale("已达到最大连接数上限: " + absoluteMax);
    }

    // 使用率超过阈值
    boolean highUsage =
        current.getUsagePercentage() > properties.getScaleUp().getThresholdPercentage();

    // 有等待线程
    boolean hasQueue =
        current.getPendingThreads() >= properties.getScaleUp().getQueueLengthThreshold();

    if (highUsage || hasQueue) {
      int increment = properties.getScaleUp().getIncrementSize();
      int targetSize = Math.min(currentMax + increment, absoluteMax);

      String reason =
          String.format(
              "使用率%.1f%% %s 等待队列%d",
              current.getUsagePercentage(), highUsage ? "超过阈值" : "正常", current.getPendingThreads());

      return ScaleDecision.scaleUp(targetSize, reason);
    }

    return ScaleDecision.noScale("未达到扩容条件");
  }

  private ScaleDecision evaluateScaleDown(PoolStatistics current, List<PoolStatistics> history) {
    if (!properties.getScaleDown().isEnabled()) {
      return ScaleDecision.noScale("缩容已禁用");
    }

    int currentMax = current.getMaxPoolSize();
    int absoluteMin = properties.getScaleDown().getMinPoolSize();

    // 已达到下限
    if (currentMax <= absoluteMin) {
      consecutiveLowUsage.set(0);
      return ScaleDecision.noScale("已达到最小连接数下限: " + absoluteMin);
    }

    // 使用率低于阈值
    if (current.getUsagePercentage() < properties.getScaleDown().getThresholdPercentage()) {
      int count = consecutiveLowUsage.incrementAndGet();
      // 计算次数
      int requiredCount =
          (int)
                  (properties.getScaleDown().getIdleTime().toSeconds()
                      / properties.getMonitor().getIntervalTime().toSeconds())
              + 1; // 每10秒检查一次

      if (count >= requiredCount) {
        int decrement = properties.getScaleDown().getDecrementSize();
        int targetSize = Math.max(currentMax - decrement, absoluteMin);

        consecutiveLowUsage.set(0);
        return ScaleDecision.scaleDown(
            targetSize,
            String.format(
                "使用率持续%.1f%%低于阈值%d%%",
                current.getUsagePercentage(), properties.getScaleDown().getThresholdPercentage()));
      }
    } else {
      consecutiveLowUsage.set(0);
    }

    return ScaleDecision.noScale("未达到缩容条件");
  }

  public boolean executeScale(ScaleDecision decision) {
    try {
      HikariConfigMXBean configMXBean = dataSource.getHikariConfigMXBean();
      int currentSize = configMXBean.getMaximumPoolSize();
      int targetSize = decision.getTargetPoolSize();

      if (currentSize == targetSize) {
        return false;
      }

      // 修改最大连接数
      configMXBean.setMaximumPoolSize(targetSize);

      // 同时调整最小空闲连接
      if (decision.getScaleType() == ScaleDecision.ScaleType.UP) {
        int newMinIdle = Math.min(targetSize, configMXBean.getMinimumIdle() + 5);
        configMXBean.setMinimumIdle(newMinIdle);
      } else {
        int newMinIdle =
            Math.max(properties.getScaleDown().getMinPoolSize(), configMXBean.getMinimumIdle() - 5);
        configMXBean.setMinimumIdle(newMinIdle);
      }

      lastScaleTime.set(System.currentTimeMillis());

      log.info(
          "连接池{}完成: {} -> {}",
          decision.getScaleType() == ScaleDecision.ScaleType.UP ? "扩容" : "缩容",
          currentSize,
          targetSize);

      return true;

    } catch (Exception e) {
      log.error("执行连接池扩缩容失败", e);
      return false;
    }
  }
}
