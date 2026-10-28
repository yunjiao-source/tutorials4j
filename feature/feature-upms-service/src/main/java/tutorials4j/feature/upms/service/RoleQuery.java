package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.RoleSpecification.codeEqual;
import static tutorials4j.feature.upms.service.RoleSpecification.codeLike;
import static tutorials4j.feature.upms.service.RoleSpecification.createdDateGte;
import static tutorials4j.feature.upms.service.RoleSpecification.createdDateLte;
import static tutorials4j.feature.upms.service.RoleSpecification.descriptionLike;
import static tutorials4j.feature.upms.service.RoleSpecification.enabledEqual;
import static tutorials4j.feature.upms.service.RoleSpecification.nameLike;
import static tutorials4j.feature.upms.service.RoleSpecification.reservedEqual;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import tutorials4j.feature.upms.entity.RoleEntity;
import tutorials4j.toolkit.core.model.YesNoEnum;
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
  private String keyword;
  private String code;
  private Instant createdDateBegin;
  private Instant createdDateEnd;
  private YesNoEnum enabled;
  private YesNoEnum reserved;

  @Override
  protected void addSpec() {
    addSpec(
            StringUtils.isNotBlank(keyword),
            codeLike(keyword).or(nameLike(keyword)).or(descriptionLike(keyword)))
        .addSpec(codeEqual(code))
        .addSpec(enabledEqual(enabled))
        .addSpec(reservedEqual(reserved))
        .addSpec(createdDateGte(createdDateBegin))
        .addSpec(createdDateLte(createdDateEnd));
  }
}
