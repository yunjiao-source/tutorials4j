package tutorials4j.framework.assy.schedule;

import java.time.Instant;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class TaskStatistics {
  private String taskId;
  private Instant lastStartTime;
  private Instant lastCompleteTime;
  private int successCount;
  private int failureCount;
  private long totalDurationMs;
  private long avgDurationMs;
  private String nextFireTime;
}
