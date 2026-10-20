package tutorials4j.feature.upms.service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.toolkit.core.model.GenderEnum;
import tutorials4j.toolkit.core.model.UserStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UserQuery {
  private String name;

  private GenderEnum gender;

  private Set<UserStatus> statuses;

  private Duration accountExpire;

  private Duration credentialsExpire;

  public Specification<UserEntity> buildSpecification() {
    var specList =
        List.of(
            UserSpecification.nameLike(name),
            UserSpecification.genderEqual(gender),
            UserSpecification.statusIn(statuses),
            UserSpecification.accountExpireAt(accountExpire),
            UserSpecification.credentialsExpireAt(credentialsExpire));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
