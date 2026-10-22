package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class PermissionCreateDTO extends PermissionUpdateDTO {
  @NotBlank(message = "代码是必须的")
  private String code;
}
