package tutorials4j.framework.feature.signin.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SignInResultSpecification {
  public static Specification<SignInResultEntity> accountEqual(String account) {
    return (root, query, cb) -> accountEqual(root, account, cb);
  }

  public static Predicate accountEqual(
      Path<SignInResultEntity> path, String account, CriteriaBuilder cb) {
    if (account == null) {
      return cb.conjunction();
    }
    return cb.equal(path.get("account"), account);
  }

  public static Specification<SignInResultEntity> sourceEqual(String source) {
    return (root, query, cb) -> accountEqual(root, source, cb);
  }

  public static Predicate sourceEqual(
      Path<SignInResultEntity> path, String source, CriteriaBuilder cb) {
    if (source == null) {
      return cb.conjunction();
    }
    return cb.equal(path.get("source"), source);
  }

  public static Specification<SignInResultEntity> signDateGte(LocalDate startSignDate) {
    return (root, query, cb) -> signDateGte(root, startSignDate, cb);
  }

  public static Predicate signDateGte(
      Path<SignInResultEntity> path, LocalDate startSignDate, CriteriaBuilder cb) {
    if (startSignDate == null) {
      return cb.conjunction();
    }
    return cb.greaterThanOrEqualTo(path.get("signDate"), startSignDate);
  }

  public static Specification<SignInResultEntity> signDateLte(LocalDate endSignDate) {
    return (root, query, cb) -> signDateLte(root, endSignDate, cb);
  }

  public static Predicate signDateLte(
      Path<SignInResultEntity> path, LocalDate endSignDate, CriteriaBuilder cb) {
    if (endSignDate == null) {
      return cb.conjunction();
    }
    return cb.lessThanOrEqualTo(path.get("signDate"), endSignDate);
  }
}
