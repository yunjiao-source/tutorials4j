package tutorials4j.feature.oauth.model;

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
public class RoleCreateModel extends RoleUpdateModel {
  @NotBlank(message = "代码是必须的")
  private String code;
}
