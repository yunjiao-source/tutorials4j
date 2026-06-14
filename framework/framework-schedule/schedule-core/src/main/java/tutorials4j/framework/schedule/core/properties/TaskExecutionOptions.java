package tutorials4j.framework.schedule.core.properties;

import java.time.Duration;
import java.time.Instant;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class TaskExecutionOptions {
  private Duration initialDelay = Duration.ofSeconds(30);
  private Integer maxFailureCount;
  private Integer MaxExecutionCount;
  private Instant dueDate;
}
