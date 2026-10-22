package tutorials4j.feature.basic.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class DictTypeDTO {
  private String parentId;

  @NotBlank(message = "名称是必须的")
  private String name;

  @NotBlank(message = "代码是必须的")
  private String code;

  @NotNull(message = "是否树是必须的")
  private YesNoEnum isTree;

  private String description;

  private Integer rank;
}
