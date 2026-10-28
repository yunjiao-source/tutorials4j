package tutorials4j.feature.basic.service;

import static tutorials4j.feature.basic.service.CompanySpecification.companyTypeLike;
import static tutorials4j.feature.basic.service.CompanySpecification.nameLike;
import static tutorials4j.feature.basic.service.CompanySpecification.shortNameLike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.basic.entity.CompanyEntity;
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
public class CompanyQuery extends BaseQuery<CompanyEntity> {
  private String name;
  private String shortName;
  private String companyType;

  @Override
  protected void addSpec() {
    addSpec(nameLike(name)).addSpec(shortNameLike(shortName)).addSpec(companyTypeLike(companyType));
  }
}
