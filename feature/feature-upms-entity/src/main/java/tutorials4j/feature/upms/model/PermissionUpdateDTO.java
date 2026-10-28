package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class PermissionUpdateDTO {

  @NotBlank(message = "名称是必须的")
  private String name;

  private String description;
}
