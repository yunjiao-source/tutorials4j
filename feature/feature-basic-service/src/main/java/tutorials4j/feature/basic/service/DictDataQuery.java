package tutorials4j.feature.basic.service;

import static tutorials4j.feature.basic.service.DictDataSpecification.allRoot;
import static tutorials4j.feature.basic.service.DictDataSpecification.codeEqual;
import static tutorials4j.feature.basic.service.DictDataSpecification.descriptionLike;
import static tutorials4j.feature.basic.service.DictDataSpecification.dictTypeIdEqual;
import static tutorials4j.feature.basic.service.DictDataSpecification.enabledEqual;
import static tutorials4j.feature.basic.service.DictDataSpecification.nameLike;
import static tutorials4j.feature.basic.service.DictDataSpecification.parentIdEqual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.basic.entity.DictDataEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;
import tutorials4j.toolkit.data.hibernate.domain.BaseQuery;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDataQuery extends BaseQuery<DictDataEntity> {
  private YesNoEnum allRoot;

  private String parentId;

  private String name;
  private String code;

  private String description;

  private YesNoEnum enabled;

  private String dictTypeId;

  @Override
  protected void addSpec() {
    addSpec(YesNoEnum.isTrue(allRoot), allRoot())
        .addSpec(parentIdEqual(parentId))
        .addSpec(nameLike(name))
        .addSpec(codeEqual(code))
        .addSpec(dictTypeIdEqual(dictTypeId))
        .addSpec(descriptionLike(description))
        .addSpec(enabledEqual(enabled));
  }
}
