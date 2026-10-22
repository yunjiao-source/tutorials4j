package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.upms.entity.UserEntity;
import tutorials4j.toolkit.core.model.GenderEnum;
import tutorials4j.toolkit.core.model.UserStatus;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class UserSpecification {
  public static Specification<UserEntity> nameLike(String name) {
    return (root, query, cb) ->
        StringUtils.isBlank(name) ? null : cb.like(root.get("name"), like(name));
  }

  public static Specification<UserEntity> genderEqual(GenderEnum gender) {
    return (root, query, cb) -> gender == null ? null : cb.equal(root.get("gender"), gender);
  }

  public static Specification<UserEntity> statusIn(Collection<UserStatus> allStatus) {
    return (root, query, cb) -> allStatus == null ? null : cb.in(root.get("status").in(allStatus));
  }

  public static Specification<UserEntity> accountExpireAt(Duration accountExpire) {
    return (root, query, cb) ->
        accountExpire == null
            ? null
            : cb.lessThanOrEqualTo(root.get("accountExpireAt"), Instant.now().plus(accountExpire));
  }

  public static Specification<UserEntity> credentialsExpireAt(Duration credentialsExpire) {
    return (root, query, cb) ->
        credentialsExpire == null
            ? null
            : cb.lessThanOrEqualTo(
                root.get("credentialsExpireAt"), Instant.now().plus(credentialsExpire));
  }
}
