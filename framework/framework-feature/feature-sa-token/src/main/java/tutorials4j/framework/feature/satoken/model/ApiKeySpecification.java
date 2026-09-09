package tutorials4j.framework.feature.satoken.model;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ApiKeySpecification {
  public static Specification<ApiKeyEntity> titleLike(String title) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(title)) {
        return cb.conjunction();
      }
      return cb.like(root.get("title"), like(title));
    };
  }

  public static Specification<ApiKeyEntity> namespaceEqual(String namespace) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(namespace)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("namespace"), namespace);
    };
  }

  public static Specification<ApiKeyEntity> loginIdEqual(String loginId) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(loginId)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("loginId"), loginId);
    };
  }

  public static Specification<ApiKeyEntity> isValidEqual(Boolean isValid) {
    return (root, query, cb) -> {
      if (isValid == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("isValid"), isValid);
    };
  }

  public static Specification<ApiKeyEntity> createTimeBegin(Date createTimeBegin) {
    return (root, query, cb) -> {
      if (createTimeBegin == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("createTime"), createTimeBegin);
    };
  }

  public static Specification<ApiKeyEntity> createTimeEnd(Date createTimeEnd) {
    return (root, query, cb) -> {
      if (createTimeEnd == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("createTime"), createTimeEnd);
    };
  }
}
