package tutorials4j.springboot3.web.hikariscaler.scaler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知服务
 *
 * @author Yun Jiao
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
  private final DynamicPoolProperties properties;

  public void notifyScale(ScaleDecision decision, PoolStatistics stats) {
    if (!properties.getAlert().isEnabled()) {
      return;
    }

    String message =
        String.format(
            "【连接池%s通知】\n时间: %s\n类型: %s\n目标大小: %d\n当前状态: 活跃=%d, 空闲=%d, 等待=%d\n原因: %s",
            decision.getScaleType() == ScaleDecision.ScaleType.UP ? "扩容" : "缩容",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            decision.getScaleType(),
            decision.getTargetPoolSize(),
            stats.getActiveConnections(),
            stats.getIdleConnections(),
            stats.getPendingThreads(),
            decision.getReason());

    log.info(message);

    // 这里可以集成邮件、钉钉等通知渠道
    sendNotification(message);
  }

  public void notifyHighUsage(PoolStatistics stats) {
    String message =
        String.format(
            "【连接池高使用率告警】\n使用率: %.1f%%\n活跃连接: %d\n最大连接: %d\n等待队列: %d",
            stats.getUsagePercentage(),
            stats.getActiveConnections(),
            stats.getMaxPoolSize(),
            stats.getPendingThreads());

    log.warn(message);
    sendNotification(message);
  }

  public void notifyQueueWait(PoolStatistics stats) {
    String message =
        String.format(
            "【连接池等待队列告警】\n等待线程: %d\n活跃连接: %d\n空闲连接: %d",
            stats.getPendingThreads(), stats.getActiveConnections(), stats.getIdleConnections());

    log.warn(message);
    sendNotification(message);
  }

  private void sendNotification(String message) {
    // 实现邮件、钉钉等通知逻辑
    // TODO: 集成实际的通知渠道
    log.info(">>> 发送通知消息：{}", message);
  }
}
