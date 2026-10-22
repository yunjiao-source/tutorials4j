package tutorials4j.feature.upms.service;

import static tutorials4j.feature.upms.service.UserSpecification.accountExpireAt;
import static tutorials4j.feature.upms.service.UserSpecification.credentialsExpireAt;
import static tutorials4j.feature.upms.service.UserSpecification.genderEqual;
import static tutorials4j.feature.upms.service.UserSpecification.nameLike;
import static tutorials4j.feature.upms.service.UserSpecification.statusIn;

import java.time.Duration;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.toolkit.core.model.GenderEnum;
import tutorials4j.toolkit.core.model.UserStatus;
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
public class UserQuery extends BaseQuery<UserEntity> {
  private String name;

  private GenderEnum gender;

  private Set<UserStatus> statuses;

  private Duration accountExpire;

  private Duration credentialsExpire;

  @Override
  protected void addSpec() {
    addSpec(nameLike(name))
        .addSpec(genderEqual(gender))
        .addSpec(statusIn(statuses))
        .addSpec(accountExpireAt(accountExpire))
        .addSpec(credentialsExpireAt(credentialsExpire));
  }
}
