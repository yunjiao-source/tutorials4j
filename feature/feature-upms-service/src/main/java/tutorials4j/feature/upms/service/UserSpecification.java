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
    return (root, query, cb) -> {
      if (StringUtils.isBlank(name)) {
        return cb.conjunction();
      }
      return cb.like(root.get("name"), like(name));
    };
  }

  public static Specification<UserEntity> genderEqual(GenderEnum gender) {
    return (root, query, cb) -> {
      if (gender == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("gender"), gender);
    };
  }

  public static Specification<UserEntity> statusIn(Collection<UserStatus> statuses) {
    return (root, query, cb) -> {
      if (statuses == null) {
        return cb.conjunction();
      }
      return cb.in(root.get("status").in(statuses));
    };
  }

  public static Specification<UserEntity> accountExpireAt(Duration accountExpire) {
    return (root, query, cb) -> {
      if (accountExpire == null) {
        return cb.conjunction();
      }

      Instant endTime = Instant.now().plus(accountExpire);
      return cb.lessThanOrEqualTo(root.get("accountExpireAt"), endTime);
    };
  }

  public static Specification<UserEntity> credentialsExpireAt(Duration credentialsExpire) {
    return (root, query, cb) -> {
      if (credentialsExpire == null) {
        return cb.conjunction();
      }

      Instant endTime = Instant.now().plus(credentialsExpire);
      return cb.lessThanOrEqualTo(root.get("credentialsExpireAt"), endTime);
    };
  }
}
