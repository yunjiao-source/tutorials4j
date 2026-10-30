package tutorials4j.feature.upms.service;

import static tutorials4j.toolkit.data.util.JPAUtils.like;

import java.time.Instant;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.feature.core.model.AuthStatus;
import tutorials4j.feature.upms.entity.AuthHistoryEntity;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public class AuthHistorySpecification {

  public static Specification<AuthHistoryEntity> userFullNameLike(String userFullName) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(userFullName)) {
        return null;
      }
      return cb.like(root.get("user").get("name"), like(userFullName));
    };
  }

  public static Specification<AuthHistoryEntity> usernameEqual(String username) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(username)) {
        return null;
      }
      return cb.equal(root.get("user").get("username"), username);
    };
  }

  public static Specification<AuthHistoryEntity> userIdEqual(String userId) {
    return (root, query, cb) -> userId == null ? null : cb.equal(root.get("user_id"), userId);
  }

  public static Specification<AuthHistoryEntity> authStatusEqual(AuthStatus authStatus) {
    return (root, query, cb) ->
        authStatus == null ? null : cb.equal(root.get("authStatus"), authStatus);
  }

  public static Specification<AuthHistoryEntity> authStatusIn(
      Collection<AuthStatus> authStatusSet) {
    return (root, query, cb) ->
        authStatusSet == null ? null : root.get("authStatus").in(authStatusSet);
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
