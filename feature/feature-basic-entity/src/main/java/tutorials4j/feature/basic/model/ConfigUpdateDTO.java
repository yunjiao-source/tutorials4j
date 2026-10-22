package tutorials4j.feature.basic.model;

import jakarta.validation.constraints.NotBlank;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ConfigUpdateDTO {
  @NotBlank(message = "代码是必须的")
  private String name;

  private String value;
  private String category;
  private String description;

  private YesNoEnum enabled;
}
