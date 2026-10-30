package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.PermissionSpecification.codeEqual;
import static tutorials4j.feature.upms.service.PermissionSpecification.codeLike;
import static tutorials4j.feature.upms.service.PermissionSpecification.descriptionLike;
import static tutorials4j.feature.upms.service.PermissionSpecification.enabledEqual;
import static tutorials4j.feature.upms.service.PermissionSpecification.nameLike;
import static tutorials4j.feature.upms.service.PermissionSpecification.reservedEqual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.feature.upms.entity.PermissionEntity;
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
public class PermissionQuery extends BaseQuery<PermissionEntity> {
  private String keyword;

  private String code;

  private String name;

  private String description;

  private YesNoEnum enabled;

  private YesNoEnum reserved;

  @Override
  protected void addSpec() {
    addSpec(
            StringUtils.isNotBlank(keyword),
            codeLike(keyword).or(nameLike(keyword).or(descriptionLike(keyword))))
        .addSpec(codeEqual(code))
        .addSpec(nameLike(name))
        .addSpec(enabledEqual(enabled))
        .addSpec(reservedEqual(reserved));
  }
}
