package tutorials4j.feature.basic.model;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import tutorials4j.feature.basic.entity.DictDataEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DictDataVO extends BasicVO {

  private String parentId;

  private String name;

  private String code;

  private String description;

  private Integer ordered;

  private String dictTypeId;

  private Map<String, String> jsonData;

  private DictTypeVO dictType;

  public static DictDataVO of(DictDataEntity entity) {
    Assert.notNull(entity, "entity must not be null");

    DictDataVO vo = new DictDataVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }

  public static List<DictDataVO> of(List<DictDataEntity> entities) {
    Assert.notNull(entities, "entities must not be null");

    return entities.stream().map(DictDataVO::of).toList();
  }

  public static List<Tree<String>> toTree(List<DictDataVO> dictDataVOS, String rootId) {
    Assert.notNull(dictDataVOS, "dictDataVOS must not be null");

    return TreeUtil.build(
        dictDataVOS,
        rootId,
        (dictData, tree) -> {
          tree.setId(dictData.getId());
          tree.setParentId(dictData.getParentId());
          tree.setName(dictData.getName());
          if (dictData.getOrdered() != null) {
            tree.setWeight(dictData.getOrdered());
          }

          tree.putExtra("code", dictData.getCode());
          tree.putExtra("description", dictData.getDescription());
          tree.putExtra("dictTypeId", dictData.getDictTypeId());
          tree.putExtra("jsonData", dictData.getJsonData());
          tree.putExtra("dictType", dictData.getDictType());
        });
  }

  public static List<Tree<String>> toTree(List<DictDataVO> dictDataVOS) {
    return toTree(dictDataVOS, null);
  }

  public static DictDataVO withDictType(DictDataEntity dictDataEntity) {
    DictDataVO dictDataVO = of(dictDataEntity);
    if (dictDataEntity.getDictType() != null) {
      dictDataVO.setDictType(DictTypeVO.of(dictDataEntity.getDictType()));
    }
    return dictDataVO;
  }
}
