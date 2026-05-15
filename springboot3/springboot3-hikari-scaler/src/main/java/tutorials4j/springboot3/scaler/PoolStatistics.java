package tutorials4j.springboot3.scaler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 连接池指标统计
 *
 * @author Yun Jiao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoolStatistics {
  private long timestamp;
  private int activeConnections;
  private int idleConnections;
  private int totalConnections;
  private int pendingThreads;
  private int maxPoolSize;
  private int minIdleConnections;
  private double usagePercentage;

  public int getQueueLength() {
    return pendingThreads;
  }

  public boolean isHealthy() {
    return usagePercentage < 80 && pendingThreads == 0;
  }

  public boolean isCritical() {
    return usagePercentage > 90 || pendingThreads > 10;
  }
}
