package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class PermissionCreateDTO extends PermissionUpdateDTO {
  @NotBlank(message = "代码是必须的")
  private String code;
}
