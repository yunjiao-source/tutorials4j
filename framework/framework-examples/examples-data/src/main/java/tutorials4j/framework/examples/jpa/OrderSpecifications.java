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

public class OrderSpecifications {

  public static Specification<Order> amountGreaterThan(BigDecimal amount) {
    return (root, query, cb) -> amountGreaterThan(root, amount, cb);
  }

  static Predicate amountGreaterThan(Path<Order> orderPath, BigDecimal amount, CriteriaBuilder cb) {
    if (amount == null) {
      return cb.conjunction();
    }
    return cb.greaterThan(orderPath.get("amount"), amount);
  }

  public static Specification<Order> timeBetween(LocalDateTime start, LocalDateTime end) {
    return (root, query, cb) -> timeBetween(root, start, end, cb);
  }

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

  public static Specification<Order> usernameLike(String username) {
    return (root, query, cb) -> {
      Join<Order, User> userJoin = leftJoin(root, "user");
      return UserSpecifications.usernameLike(userJoin, username, cb);
    };
  }

  public static Specification<Order> emailEquals(String email) {
    return (root, query, cb) -> {
      Join<Order, User> userJoin = leftJoin(root, "user");
      return UserSpecifications.emailEquals(userJoin, email, cb);
    };
  }

  public static Specification<Order> ageBetween(Integer minAge, Integer maxAge) {
    return (root, query, cb) -> {
      Join<Order, User> userJoin = leftJoin(root, "user");
      return UserSpecifications.ageBetween(userJoin, minAge, maxAge, cb);
    };
  }

  static Specification<Order> fetchUser() {
    return (root, query, cb) -> {
      if (!Long.class.equals(query.getResultType())) {
        root.fetch("user", JoinType.LEFT);
      }
      return cb.conjunction();
    };
  }

  // ========== 动态组合所有条件 ==========
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
