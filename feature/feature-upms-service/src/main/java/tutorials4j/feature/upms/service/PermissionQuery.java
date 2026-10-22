package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.PermissionSpecification.codeLike;
import static tutorials4j.feature.upms.service.PermissionSpecification.nameLike;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.upms.entity.PermissionEntity;
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
public class PermissionQuery extends BaseQuery<PermissionEntity> {
  private String code;

  private String name;

  @Override
  protected void addSpec() {
    addSpec(codeLike(code)).addSpec(nameLike(name));
  }
}
