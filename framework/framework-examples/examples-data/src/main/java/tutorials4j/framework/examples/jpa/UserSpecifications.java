package tutorials4j.framework.examples.jpa;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public interface UserSpecifications {

  static Specification<User> usernameLike(String username) {
    return (root, query, cb) -> usernameLike(root, username, cb);
  }

  static Predicate usernameLike(Path<User> userPath, String username, CriteriaBuilder cb) {
    if (StringUtils.isBlank(username)) {
      return cb.conjunction();
    }
    return cb.like(userPath.get("username"), like(username));
  }

  static Specification<User> emailEquals(String email) {
    return (root, query, cb) -> emailEquals(root, email, cb);
  }

  static Predicate emailEquals(Path<User> userPath, String email, CriteriaBuilder cb) {
    if (StringUtils.isBlank(email)) {
      return cb.conjunction();
    }
    return cb.equal(userPath.get("email"), email);
  }

  public static Specification<User> ageBetween(Integer minAge, Integer maxAge) {
    return (root, query, cb) -> {
      if (minAge == null && maxAge == null) return null;
      List<Predicate> preds = new ArrayList<>();
      if (minAge != null) preds.add(cb.ge(root.get("age"), minAge));
      if (maxAge != null) preds.add(cb.le(root.get("age"), maxAge));
      return cb.and(preds.toArray(new Predicate[0]));
    };
  }

  static Predicate ageBetween(
      Path<User> userPath, Integer minAge, Integer maxAge, CriteriaBuilder cb) {
    if (minAge == null && maxAge == null) {
      return cb.conjunction();
    }
    List<Predicate> preds = new ArrayList<>();
    if (minAge != null) preds.add(cb.ge(userPath.get("age"), minAge));
    if (maxAge != null) preds.add(cb.le(userPath.get("age"), maxAge));
    return cb.and(preds.toArray(new Predicate[0]));
  }
}
