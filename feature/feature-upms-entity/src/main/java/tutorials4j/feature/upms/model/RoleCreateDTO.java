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
public class RoleCreateDTO extends RoleUpdateDTO {
  @NotBlank(message = "代码是必须的")
  private String code;

  private YesNoEnum isReserved;
}
