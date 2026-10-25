package tutorials4j.feature.upms.model;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.upms.entity.OrganizeCategory;
import tutorials4j.feature.upms.entity.OrganizeEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class OrganizeVO extends UpmsVO {
  private String parentId;

  private String name;

  private String shortName;

  private String code;

  private Integer ordered;

  private OrganizeCategory category;

  public static OrganizeVO of(OrganizeEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    OrganizeVO vo = new OrganizeVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static List<OrganizeVO> of(Collection<OrganizeEntity> entities) {
    Assert.notNull(entities, "entity must not be null");

    return entities.stream().map(OrganizeVO::of).toList();
  }

  public static List<Tree<String>> toTree(List<OrganizeVO> organizes, String rootId) {
    Assert.notNull(organizes, "organizes must not be null");

    return TreeUtil.build(
        organizes,
        rootId,
        (organize, tree) -> {
          tree.setId(organize.getId());
          tree.setParentId(organize.getParentId());
          tree.setName(organize.getName());
          if (organize.getOrdered() != null) {
            tree.setWeight(organize.getOrdered());
          }

          tree.putExtra("shortName", organize.getShortName());
          tree.putExtra("code", organize.getCode());
          tree.putExtra("ordered", organize.getOrdered());
          tree.putExtra("category", organize.getCategory());
        });
  }

  /** 顶级 parentId 为 null 时的便捷重载 */
  public static List<Tree<String>> toTree(List<OrganizeVO> organizes) {
    return toTree(organizes, null);
  }
}
