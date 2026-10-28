package tutorials4j.feature.upms.model;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.MenuEntity;
import tutorials4j.feature.upms.entity.MenuType;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class MenuVO extends UpmsVO {
  private String parentId;

  private String path;

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

  public static MenuVO of(MenuEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    MenuVO vo = new MenuVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static List<MenuVO> of(Collection<MenuEntity> entities) {
    Assert.notNull(entities, "entity must not be null");

    return entities.stream().map(MenuVO::of).toList();
  }

  public static List<Tree<String>> toTree(List<MenuVO> menus, String rootId) {
    Assert.notNull(menus, "menus must not be null");

    return TreeUtil.build(
        menus,
        rootId,
        (menu, tree) -> {
          tree.setId(menu.getId());
          tree.setParentId(menu.getParentId());
          tree.setName(menu.getName());
          if (menu.getOrder() != null) {
            tree.setWeight(menu.getOrder());
          }

          tree.putExtra("pid", menu.getParentId());

          Opt.ofBlankAble(menu.getPath()).ifPresent(value -> tree.putExtra("path", value));
          Opt.ofBlankAble(menu.getName()).ifPresent(value -> tree.putExtra("name", value));
          Opt.ofBlankAble(menu.getComponent())
              .ifPresent(value -> tree.putExtra("component", value));
          Opt.ofBlankAble(menu.getRedirect()).ifPresent(value -> tree.putExtra("redirect", value));
          Opt.ofNullable(menu.getType()).ifPresent(value -> tree.putExtra("type", value.name()));

          Map<String, Object> metaMap = new HashMap<>();
          Opt.ofBlankAble(menu.getActiveIcon())
              .ifPresent(value -> metaMap.put("activeIcon", value));
          Opt.ofBlankAble(menu.getActivePath())
              .ifPresent(value -> metaMap.put("activePath", value));
          Opt.ofNullable(menu.getAffixTab()).ifPresent(value -> metaMap.put("affixTab", value));
          Opt.ofNullable(menu.getAffixTabOrder())
              .ifPresent(value -> metaMap.put("affixTabOrder", value));
          Opt.ofBlankAble(menu.getAuthority())
              .ifPresent(value -> metaMap.put("authority", value.split(",")));
          Opt.ofBlankAble(menu.getBadge()).ifPresent(value -> metaMap.put("badge", value));
          Opt.ofBlankAble(menu.getBadgeType()).ifPresent(value -> metaMap.put("badgeType", value));
          Opt.ofBlankAble(menu.getBadgeVariants())
              .ifPresent(value -> metaMap.put("badgeVariants", value));
          Opt.ofNullable(menu.getHideChildrenInMenu())
              .ifPresent(value -> metaMap.put("hideChildrenInMenu", value));
          Opt.ofNullable(menu.getHideInBreadcrumb())
              .ifPresent(value -> metaMap.put("hideInBreadcrumb", value));
          Opt.ofNullable(menu.getHideInMenu()).ifPresent(value -> metaMap.put("hideInMenu", value));
          Opt.ofNullable(menu.getHideInTab()).ifPresent(value -> metaMap.put("hideInTab", value));
          Opt.ofBlankAble(menu.getIcon()).ifPresent(value -> metaMap.put("icon", value));
          Opt.ofBlankAble(menu.getIframeSrc()).ifPresent(value -> metaMap.put("iframeSrc", value));
          Opt.ofNullable(menu.getKeepAlive()).ifPresent(value -> metaMap.put("keepAlive", value));
          Opt.ofBlankAble(menu.getLink()).ifPresent(value -> metaMap.put("link", value));
          Opt.ofNullable(menu.getMaxNumOfOpenTab())
              .ifPresent(value -> metaMap.put("maxNumOfOpenTab", value));
          Opt.ofNullable(menu.getNoBasicLayout())
              .ifPresent(value -> metaMap.put("noBasicLayout", value));
          Opt.ofNullable(menu.getOpenInNewWindow())
              .ifPresent(value -> metaMap.put("openInNewWindow", value));
          Opt.ofNullable(menu.getOrder()).ifPresent(value -> metaMap.put("order", value));
          Opt.ofBlankAble(menu.getQuery()).ifPresent(value -> metaMap.put("query", value));
          Opt.ofBlankAble(menu.getTitle()).ifPresent(value -> metaMap.put("title", value));

          tree.putExtra("meta", metaMap);
        });
  }

  public static List<Tree<String>> toTree(List<MenuVO> menus) {
    return toTree(menus, null);
  }
}
