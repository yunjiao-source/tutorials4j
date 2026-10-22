package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.RoleSpecification.codeLike;
import static tutorials4j.feature.upms.service.RoleSpecification.nameLike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.toolkit.data.hibernate.domain.BaseQuery;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleQuery extends BaseQuery<RoleEntity> {
  private String code;
  private String name;

  @Override
  protected void addSpec() {
    addSpec(codeLike(code)).addSpec(nameLike(name));
  }
}
