package tutorials4j.framework.feature.signin.jpa;

import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * 签到结果查询条件的 JPA Specification 工厂。
 *
 * <p>提供按账号、来源、签到日期区间构建查询条件的方法；参数为空时返回恒真条件。
 *
 * @author Yun Jiao
 */
public class SignInResultSpecification {
  /**
   * 构建账号相等的查询条件。
   *
   * @param account 账号，为空时不参与过滤
   * @return 对应的 Specification
   */
  public static Specification<SignInResultEntity> accountEqual(String account) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(account)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("account"), account);
    };
  }

  /**
   * 构建来源相等的查询条件。
   *
   * @param source 来源，为空时不参与过滤
   * @return 对应的 Specification
   */
  public static Specification<SignInResultEntity> sourceEqual(String source) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(source)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("source"), source);
    };
  }

  /**
   * 构建签到日期大于等于指定日期的查询条件。
   *
   * @param startSignDate 起始签到日期，为 null 时不参与过滤
   * @return 对应的 Specification
   */
  public static Specification<SignInResultEntity> signDateGte(LocalDate startSignDate) {
    return (root, query, cb) -> {
      if (startSignDate == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("signDate"), startSignDate);
    };
  }

  /**
   * 构建签到日期小于等于指定日期的查询条件。
   *
   * @param endSignDate 截止签到日期，为 null 时不参与过滤
   * @return 对应的 Specification
   */
  public static Specification<SignInResultEntity> signDateLte(LocalDate endSignDate) {
    return (root, query, cb) -> {
      if (endSignDate == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("signDate"), endSignDate);
    };
  }
}
