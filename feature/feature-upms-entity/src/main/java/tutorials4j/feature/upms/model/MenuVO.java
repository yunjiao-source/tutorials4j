package tutorials4j.feature.upms.model;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.MenuEntity;
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

  private String title;

  private String type;

  private String icon;

  private Integer rank;

  private YesNoEnum haveChild;

  private YesNoEnum notKeepAlive;

  private YesNoEnum hideAllChild;

  private YesNoEnum detailContent;
  private YesNoEnum ignoreAuth;

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
          if (menu.getRank() != null) {
            tree.setWeight(menu.getRank());
          }

          tree.putExtra("path", menu.getPath());
          tree.putExtra("component", menu.getComponent());
          tree.putExtra("redirect", menu.getRedirect());
          tree.putExtra("title", menu.getTitle());
          tree.putExtra("type", menu.getType());
          tree.putExtra("icon", menu.getIcon());
          tree.putExtra("haveChild", menu.getHaveChild());
          tree.putExtra("notKeepAlive", menu.getNotKeepAlive());
          tree.putExtra("hideAllChild", menu.getHideAllChild());
          tree.putExtra("detailContent", menu.getDetailContent());
          tree.putExtra("ignoreAuth", menu.getIgnoreAuth());
        });
  }

  public static List<Tree<String>> toTree(List<MenuVO> menus) {
    return toTree(menus, null);
  }
}
