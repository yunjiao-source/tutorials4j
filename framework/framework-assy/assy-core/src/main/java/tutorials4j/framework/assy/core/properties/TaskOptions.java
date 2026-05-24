package tutorials4j.framework.assy.core.properties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskOptions {
  @EqualsAndHashCode.Include private String name;
  private String cronExpression;
  private boolean enabled;
  private String description;
  private String type;
  private Map<String, String> metadata = new HashMap<>();

  private LockOptions lock = new LockOptions();
  private TriggerOptions trigger = new TriggerOptions();

  @Data
  public static class LockOptions {
    private boolean enabled;
    private Duration waitTime;
    private Duration expireTime;
  }

  @Data
  public static class TriggerOptions {
    private Integer executionLimit;
    private LocalDateTime executionDeadline;
  }
}
