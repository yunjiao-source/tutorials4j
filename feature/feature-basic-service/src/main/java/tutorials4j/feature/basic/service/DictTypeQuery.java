package tutorials4j.feature.basic.service;

import static tutorials4j.feature.basic.service.DictTypeSpecification.allRoot;
import static tutorials4j.feature.basic.service.DictTypeSpecification.codeLike;
import static tutorials4j.feature.basic.service.DictTypeSpecification.descriptionLike;
import static tutorials4j.feature.basic.service.DictTypeSpecification.enabledEqual;
import static tutorials4j.feature.basic.service.DictTypeSpecification.nameLike;
import static tutorials4j.feature.basic.service.DictTypeSpecification.parentIdEqual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.basic.entity.DictTypeEntity;
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
public class DictTypeQuery extends BaseQuery<DictTypeEntity> {
  private YesNoEnum allRoot;

  private String parentId;

  private String name;
  private String code;

  private String description;

  private YesNoEnum enabled;

  @Override
  protected void addSpec() {
    addSpec(YesNoEnum.isTrue(allRoot), allRoot())
        .addSpec(parentIdEqual(parentId))
        .addSpec(nameLike(name))
        .addSpec(codeLike(code))
        .addSpec(descriptionLike(description))
        .addSpec(enabledEqual(enabled));
  }
}
