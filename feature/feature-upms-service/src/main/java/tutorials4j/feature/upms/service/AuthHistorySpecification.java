package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.leftJoin;

import jakarta.persistence.criteria.Join;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.core.model.AuthStatus;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;
import tutorials4j.feature.upms.entity.UserEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class AuthHistorySpecification {
  public static Specification<AuthHistoryEntity> userIdEqual(String userId) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(userId)) {
        return cb.conjunction();
      }
      Join<AuthHistoryEntity, UserEntity> userJoin = leftJoin(root, "user");
      return cb.equal(userJoin.get("id"), userId);
    };
  }

  public static Specification<AuthHistoryEntity> authStatusEqual(AuthStatus authStatus) {
    return (root, query, cb) -> {
      if (authStatus == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("status"), authStatus);
    };
  }

  public static Specification<AuthHistoryEntity> createdTimeGte(Instant createTimeBegin) {
    return (root, query, cb) -> {
      if (createTimeBegin == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("createdTime"), createTimeBegin);
    };
  }

  public static Specification<AuthHistoryEntity> createdTimeLte(Instant createTimeEnd) {
    return (root, query, cb) -> {
      if (createTimeEnd == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("createdTime"), createTimeEnd);
    };
  }
}
