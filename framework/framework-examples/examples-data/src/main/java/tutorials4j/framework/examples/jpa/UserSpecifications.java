package tutorials4j.framework.examples.jpa;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * 用户查询条件（Specification）构建工具。
 *
 * <p>提供基于用户名、邮箱、年龄等属性的查询条件构造方法。
 *
 * @author Yun Jiao
 */
public interface UserSpecifications {

  /**
   * 构建用户名模糊匹配的查询条件。
   *
   * @param username 用户名关键字
   * @return 用户名模糊查询条件
   */
  static Specification<User> usernameLike(String username) {
    return (root, query, cb) -> usernameLike(root, username, cb);
  }

  /**
   * 生成用户名模糊匹配断言，用户名为空时返回恒真条件。
   *
   * @param userPath 用户路径
   * @param username 用户名关键字
   * @param cb CriteriaBuilder
   * @return 用户名模糊断言
   */
  static Predicate usernameLike(Path<User> userPath, String username, CriteriaBuilder cb) {
    if (StringUtils.isBlank(username)) {
      return cb.conjunction();
    }
    return cb.like(userPath.get("username"), like(username));
  }

  /**
   * 构建邮箱精确匹配的查询条件。
   *
   * @param email 邮箱
   * @return 邮箱精确查询条件
   */
  static Specification<User> emailEquals(String email) {
    return (root, query, cb) -> emailEquals(root, email, cb);
  }

  /**
   * 生成邮箱精确匹配断言，邮箱为空时返回恒真条件。
   *
   * @param userPath 用户路径
   * @param email 邮箱
   * @param cb CriteriaBuilder
   * @return 邮箱精确断言
   */
  static Predicate emailEquals(Path<User> userPath, String email, CriteriaBuilder cb) {
    if (StringUtils.isBlank(email)) {
      return cb.conjunction();
    }
    return cb.equal(userPath.get("email"), email);
  }

  /**
   * 构建年龄区间查询条件。
   *
   * @param minAge 最小年龄（含）
   * @param maxAge 最大年龄（含）
   * @return 年龄区间查询条件
   */
  public static Specification<User> ageBetween(Integer minAge, Integer maxAge) {
    return (root, query, cb) -> {
      if (minAge == null && maxAge == null) return null;
      List<Predicate> preds = new ArrayList<>();
      if (minAge != null) preds.add(cb.ge(root.get("age"), minAge));
      if (maxAge != null) preds.add(cb.le(root.get("age"), maxAge));
      return cb.and(preds.toArray(new Predicate[0]));
    };
  }

  /**
   * 生成年龄区间断言，起止年龄均为空时返回恒真条件。
   *
   * @param userPath 用户路径
   * @param minAge 最小年龄（含）
   * @param maxAge 最大年龄（含）
   * @param cb CriteriaBuilder
   * @return 年龄区间断言
   */
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
