package tutorials4j.framework.feature.schedule.web;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class JobCreateDTO {
  @NotBlank private String taskCode;
  @NotBlank private String classSimpleName;
  @NotBlank private String cron;
  private String description;
}
