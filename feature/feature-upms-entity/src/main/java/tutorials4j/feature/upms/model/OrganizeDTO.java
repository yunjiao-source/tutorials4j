package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tutorials4j.feature.upms.entity.OrganizeCategory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class OrganizeDTO {
  private String parentId;

  @NotBlank(message = "名称是必须的")
  private String name;

  private String shortName;

  @NotBlank(message = "代码是必须的")
  private String code;

  @NotBlank(message = "序号是必须的")
  private Integer rank;

  @NotNull(message = "分类是必须的")
  private OrganizeCategory category;

  private CompanyDTO company;
}
