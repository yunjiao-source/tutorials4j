package tutorials4j.framework.data.core.util;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import tutorials4j.framework.common.core.SymbolConsts;

/**
 * JPA 查询工具接口，提供静态方法简化 Criteria 查询中关联连接和模糊查询的构建。
 *
 * <p>所有方法均为静态方法，无需实例化即可直接调用。连接方法会优先复用实体根上已存在的同属性、同类型连接， 避免查询中产生重复的连接。
 *
 * @author Yun Jiao
 */
public interface JPAUtils {
  /**
   * 获取或创建指定实体根路径上的连接。
   *
   * <p>遍历实体根上已有的连接，若存在属性名与连接类型均相同的连接则直接复用，否则创建新的连接并返回。
   *
   * @param root 实体根
   * @param attribute 连接属性名
   * @param joinType 连接类型
   * @param <Z> 源实体类型
   * @param <X> 目标实体类型
   * @return 连接对象（复用的已有连接或新建连接）
   */
  static <Z, X> Join<Z, X> join(Root<Z> root, String attribute, JoinType joinType) {
    for (Join<?, ?> join : root.getJoins()) {
      boolean sameName = join.getAttribute().getName().equals(attribute);
      if (sameName && join.getJoinType().equals(joinType)) {
        @SuppressWarnings("unchecked")
        Join<Z, X> result = (Join<Z, X>) join;
        return result;
      }
    }
    return root.join(attribute, joinType);
  }

  /**
   * 获取或创建内连接（INNER JOIN），等价于 {@code join(root, attribute, JoinType.INNER)}。
   *
   * @param root 实体根
   * @param attribute 连接属性名
   * @param <Z> 源实体类型
   * @param <X> 目标实体类型
   * @return 内连接对象
   */
  static <Z, X> Join<Z, X> innerJoin(Root<Z> root, String attribute) {
    return join(root, attribute, JoinType.INNER);
  }

  /**
   * 获取或创建左外连接（LEFT JOIN），等价于 {@code join(root, attribute, JoinType.LEFT)}。
   *
   * @param root 实体根
   * @param attribute 连接属性名
   * @param <Z> 源实体类型
   * @param <X> 目标实体类型
   * @return 左外连接对象
   */
  static <Z, X> Join<Z, X> leftJoin(Root<Z> root, String attribute) {
    return join(root, attribute, JoinType.LEFT);
  }

  /**
   * 获取或创建右外连接（RIGHT JOIN），等价于 {@code join(root, attribute, JoinType.RIGHT)}。
   *
   * @param root 实体根
   * @param attribute 连接属性名
   * @param <Z> 源实体类型
   * @param <X> 目标实体类型
   * @return 右外连接对象
   */
  static <Z, X> Join<Z, X> rightJoin(Root<Z> root, String attribute) {
    return join(root, attribute, JoinType.RIGHT);
  }

  /**
   * 构造用于 LIKE 查询的模糊匹配字符串，格式为 {@code %property%}。
   *
   * @param property 原始属性值
   * @return 包裹 {@code %} 后的模糊匹配字符串
   */
  static String like(String property) {
    return SymbolConsts.PERCENT + property + SymbolConsts.PERCENT;
  }
}
