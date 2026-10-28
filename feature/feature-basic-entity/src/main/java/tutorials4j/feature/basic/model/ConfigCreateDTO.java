package tutorials4j.feature.basic.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class ConfigCreateDTO extends ConfigUpdateDTO {
  @NotBlank(message = "关键字是必须的")
  private String code;

  private YesNoEnum reserved;
}
