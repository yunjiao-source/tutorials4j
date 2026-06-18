package tutorials4j.framework.feature.signin.domain;

import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SignInResultSpecification {
  public static Specification<SignInResultEntity> accountEqual(String account) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(account)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("account"), account);
    };
  }

  public static Specification<SignInResultEntity> sourceEqual(String source) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(source)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("source"), source);
    };
  }

  public static Specification<SignInResultEntity> signDateGte(LocalDate startSignDate) {
    return (root, query, cb) -> {
      if (startSignDate == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("signDate"), startSignDate);
    };
  }

  public static Specification<SignInResultEntity> signDateLte(LocalDate endSignDate) {
    return (root, query, cb) -> {
      if (endSignDate == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("signDate"), endSignDate);
    };
  }
}
