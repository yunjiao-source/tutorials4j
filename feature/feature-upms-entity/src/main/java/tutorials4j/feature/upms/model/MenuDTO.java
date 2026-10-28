package tutorials4j.feature.upms.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tutorials4j.feature.upms.entity.MenuType;
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

  private MenuType type;

  private String activeIcon;

  private String activePath;

  private YesNoEnum affixTab;

  private Integer affixTabOrder;

  private String authority;

  private String badge;

  private String badgeType;

  private String badgeVariants;

  private YesNoEnum hideChildrenInMenu;

  private YesNoEnum hideInBreadcrumb;

  private YesNoEnum hideInMenu;

  private YesNoEnum hideInTab;

  private String icon;

  private String iframeSrc;

  private YesNoEnum keepAlive;

  private String link;

  private Integer maxNumOfOpenTab;

  private YesNoEnum noBasicLayout;

  private YesNoEnum openInNewWindow;

  private Integer order;

  private String query;

  private String title;
}
