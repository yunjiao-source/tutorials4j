package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
public class RoleUpdateDTO {

  @NotBlank(message = "名称是必须的")
  private String name;

  private YesNoEnum status;

  private String description;

  private YesNoEnum enabled;
}
