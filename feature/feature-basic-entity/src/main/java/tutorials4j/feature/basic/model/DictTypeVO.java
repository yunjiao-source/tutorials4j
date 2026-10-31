package tutorials4j.feature.basic.model;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.basic.entity.DictTypeEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class DictTypeVO extends BasicVO {
  private String parentId;

  private String name;

  private String code;

  private String description;

  private Integer ordered;

  private YesNoEnum isTree;

  public static DictTypeVO of(DictTypeEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    DictTypeVO vo = new DictTypeVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static List<DictTypeVO> of(List<DictTypeEntity> entities) {
    Assert.notNull(entities, "entities must not be null");

    return entities.stream().map(DictTypeVO::of).toList();
  }

  public static List<Tree<String>> toTree(List<DictTypeVO> dictTypeVOS, String rootId) {
    Assert.notNull(dictTypeVOS, "dictTypeVOS must not be null");

    return TreeUtil.build(
        dictTypeVOS,
        rootId,
        (dictType, tree) -> {
          tree.setId(dictType.getId());
          tree.setParentId(dictType.getParentId());
          tree.setName(dictType.getName());
          if (dictType.getOrdered() != null) {
            tree.setWeight(dictType.getOrdered());
          }

          tree.putExtra("isTree", dictType.getIsTree());
          tree.putExtra("code", dictType.getCode());
          tree.putExtra("description", dictType.getDescription());
          tree.putExtra("label", dictType.getName());
          tree.putExtra("value", dictType.getCode());
        });
  }

  public static List<Tree<String>> toTree(List<DictTypeVO> dictTypeVOS) {
    return toTree(dictTypeVOS, null);
  }
}
