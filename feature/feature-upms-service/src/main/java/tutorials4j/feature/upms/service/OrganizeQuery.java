package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.OrganizeSpecification.allRoot;
import static tutorials4j.feature.upms.service.OrganizeSpecification.enabledEqual;
import static tutorials4j.feature.upms.service.OrganizeSpecification.nameLike;
import static tutorials4j.feature.upms.service.OrganizeSpecification.parentIdEqual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.upms.entity.OrganizeEntity;
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
public class OrganizeQuery extends BaseQuery<OrganizeEntity> {
  private YesNoEnum allRoot;
  private String parentId;
  private String name;

  private YesNoEnum enabled;

  @Override
  protected void addSpec() {
    addSpec(parentIdEqual(parentId))
        .addSpec(YesNoEnum.isTrue(allRoot), allRoot())
        .addSpec(nameLike(name))
        .addSpec(enabledEqual(enabled));
  }
}
