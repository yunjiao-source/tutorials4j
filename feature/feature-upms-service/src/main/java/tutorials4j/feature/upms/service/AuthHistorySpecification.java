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
        return null;
      }
      Join<AuthHistoryEntity, UserEntity> userJoin = leftJoin(root, "user");
      return cb.equal(userJoin.get("id"), userId);
    };
  }

  public static Specification<AuthHistoryEntity> authStatusEqual(AuthStatus authStatus) {
    return (root, query, cb) ->
        authStatus == null ? null : cb.equal(root.get("authStatus"), authStatus);
  }

  public static Specification<AuthHistoryEntity> createdTimeGte(Instant createTimeBegin) {
    return (root, query, cb) ->
        createTimeBegin == null
            ? null
            : cb.greaterThanOrEqualTo(root.get("createdTime"), createTimeBegin);
  }

  public static Specification<AuthHistoryEntity> createdTimeLte(Instant createTimeEnd) {
    return (root, query, cb) ->
        createTimeEnd == null ? null : cb.lessThanOrEqualTo(root.get("createdTime"), createTimeEnd);
  }
}
