package tutorials4j.feature.oauth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.enums.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class RoleUpdateModel {

  @NotBlank(message = "名称是必须的")
  private String name;

  private YesNoEnum status;

  private String description;
}
