package tutorials4j.feature.basic.service;

import static tutorials4j.feature.basic.service.DictTypeSpecification.allRoot;
import static tutorials4j.feature.basic.service.DictTypeSpecification.codeLike;
import static tutorials4j.feature.basic.service.DictTypeSpecification.descriptionLike;
import static tutorials4j.feature.basic.service.DictTypeSpecification.enabledIn;
import static tutorials4j.feature.basic.service.DictTypeSpecification.nameLike;
import static tutorials4j.feature.basic.service.DictTypeSpecification.parentIdEqual;

import java.util.Objects;
import java.util.Set;
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
  private Boolean allRoot;

  private String parentId;

  private String name;
  private String code;

  private String description;

  private Set<YesNoEnum> allEnabled;

  @Override
  protected void addSpec() {
    addSpec(Objects.equals(allRoot, Boolean.TRUE), allRoot())
        .addSpec(parentIdEqual(parentId))
        .addSpec(nameLike(name))
        .addSpec(codeLike(code))
        .addSpec(descriptionLike(description))
        .addSpec(enabledIn(allEnabled));
  }
}
