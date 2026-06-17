package tutorials4j.framework.feature.schedule.web;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class JobUpdateDTO {
  @NotBlank private String classSimpleName;
  @NotBlank private String cron;
  private String description;

  private Map<String, String> metadata;
  private Duration initialDelay;
  private Integer maxFailureCount;
  private Integer maxExecutionCount;
  private Instant dueDate;
}
