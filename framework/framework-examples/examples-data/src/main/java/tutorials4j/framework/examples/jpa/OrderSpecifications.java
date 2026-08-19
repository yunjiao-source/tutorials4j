package tutorials4j.framework.examples.jpa;

import static tutorials4j.framework.data.core.util.JPAUtils.leftJoin;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

/**
 * 订单查询条件（Specification）构建工具。
 *
 * <p>提供基于订单及关联用户属性的各种查询条件构造方法，并可动态组合全部条件。
 *
 * @author Yun Jiao
 */
public class OrderSpecifications {

  /**
   * 构建订单金额大于指定值的查询条件。
   *
   * @param amount 金额阈值
   * @return 金额大于阈值的查询条件
   */
  public static Specification<Order> amountGreaterThan(BigDecimal amount) {
    return (root, query, cb) -> amountGreaterThan(root, amount, cb);
  }

  /**
   * 生成订单金额大于指定值的断言，金额为空时返回恒真条件。
   *
   * @param orderPath 订单路径
   * @param amount 金额阈值
   * @param cb CriteriaBuilder
   * @return 金额断言
   */
  static Predicate amountGreaterThan(Path<Order> orderPath, BigDecimal amount, CriteriaBuilder cb) {
    if (amount == null) {
      return cb.conjunction();
    }
    return cb.greaterThan(orderPath.get("amount"), amount);
  }

  /**
   * 构建订单时间在指定区间内的查询条件。
   *
   * @param start 开始时间（含）
   * @param end 结束时间（含）
   * @return 时间区间查询条件
   */
  public static Specification<Order> timeBetween(LocalDateTime start, LocalDateTime end) {
    return (root, query, cb) -> timeBetween(root, start, end, cb);
  }

  /**
   * 生成订单时间区间断言，起止时间均为空时返回恒真条件。
   *
   * @param orderPath 订单路径
   * @param start 开始时间（含）
   * @param end 结束时间（含）
   * @param cb CriteriaBuilder
   * @return 时间区间断言
   */
  static Predicate timeBetween(
      Path<Order> orderPath, LocalDateTime start, LocalDateTime end, CriteriaBuilder cb) {
    if (start == null && end == null) {
      return cb.conjunction();
    }
    List<Predicate> preds = new ArrayList<>();
    if (start != null) preds.add(cb.greaterThanOrEqualTo(orderPath.get("orderTime"), start));
    if (end != null) preds.add(cb.lessThanOrEqualTo(orderPath.get("orderTime"), end));
    return cb.and(preds.toArray(new Predicate[0]));
  }

  /**
   * 构建关联用户用户名模糊匹配的查询条件。
   *
   * @param username 用户名关键字
   * @return 用户名模糊查询条件
   */
  public static Specification<Order> usernameLike(String username) {
    return (root, query, cb) -> {
      Join<Order, User> userJoin = leftJoin(root, "user");
      return UserSpecifications.usernameLike(userJoin, username, cb);
    };
  }

  /**
   * 构建关联用户邮箱精确匹配的查询条件。
   *
   * @param email 邮箱
   * @return 邮箱精确查询条件
   */
  public static Specification<Order> emailEquals(String email) {
    return (root, query, cb) -> {
      Join<Order, User> userJoin = leftJoin(root, "user");
      return UserSpecifications.emailEquals(userJoin, email, cb);
    };
  }

  /**
   * 构建关联用户年龄区间查询条件。
   *
   * @param minAge 最小年龄（含）
   * @param maxAge 最大年龄（含）
   * @return 年龄区间查询条件
   */
  public static Specification<Order> ageBetween(Integer minAge, Integer maxAge) {
    return (root, query, cb) -> {
      Join<Order, User> userJoin = leftJoin(root, "user");
      return UserSpecifications.ageBetween(userJoin, minAge, maxAge, cb);
    };
  }

  /**
   * 构建预拉取关联用户的查询条件，避免查询订单时产生 N+1 问题。
   *
   * <p>当查询结果类型为统计（count）时不执行拉取。
   *
   * @return 预拉取用户的查询条件
   */
  static Specification<Order> fetchUser() {
    return (root, query, cb) -> {
      if (!Long.class.equals(query.getResultType())) {
        root.fetch("user", JoinType.LEFT);
      }
      return cb.conjunction();
    };
  }

  // ========== 动态组合所有条件 ==========
  /**
   * 动态组合所有查询条件。
   *
   * @param username 用户名（可选）
   * @param email 邮箱（可选）
   * @param minAge 最小年龄（可选）
   * @param maxAge 最大年龄（可选）
   * @param minAmount 最小订单金额（可选）
   * @param orderStartTime 下单开始时间（可选）
   * @param orderEndTime 下单结束时间（可选）
   * @param needFetchOrders 是否需要预拉取订单关联的用户数据
   * @return 组合后的查询条件
   */
  public static Specification<Order> buildSpecification(
      String username,
      String email,
      Integer minAge,
      Integer maxAge,
      BigDecimal minAmount,
      LocalDateTime orderStartTime,
      LocalDateTime orderEndTime,
      boolean needFetchOrders) {

    List<Specification<Order>> specs = new ArrayList<>();
    specs.add(usernameLike(username));
    specs.add(emailEquals(email));
    specs.add(ageBetween(minAge, maxAge));
    specs.add(amountGreaterThan(minAmount));
    specs.add(timeBetween(orderStartTime, orderEndTime));
    if (needFetchOrders) {
      specs.add(fetchUser());
    }
    // 过滤掉 null 条件
    return specs.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
