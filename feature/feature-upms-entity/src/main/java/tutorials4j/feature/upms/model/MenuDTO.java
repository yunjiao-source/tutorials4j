package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class MenuDTO {
  private String parentId;

  @NotBlank(message = "路径是必须的")
  private String path;

  @NotBlank(message = "名称是必须的")
  private String name;

  private String component;

  private String redirect;

  private String title;

  private String type;

  private String icon;

  private YesNoEnum haveChild;

  private YesNoEnum notKeepAlive;

  private YesNoEnum hideAllChild;

  private YesNoEnum detailContent;

  private YesNoEnum ignoreAuth;

  private Integer rank;
}
