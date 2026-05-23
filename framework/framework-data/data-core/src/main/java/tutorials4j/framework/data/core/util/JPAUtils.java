package tutorials4j.framework.data.core.util;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import tutorials4j.framework.common.core.SymbolConsts;

/**
 * JPA 查询工具接口，提供静态方法简化 Criteria 查询中关联连接和模糊查询的构建。
 *
 * @author Yun Jiao
 */
public interface JPAUtils {
  /**
   * 获取或创建指定实体根路径上的连接。
   *
   * <p>会先检查是否已存在相同属性和连接类型的连接，若存在则复用，否则创建新连接。
   *
   * @param root 实体根
   * @param attribute 连接属性名
   * @param joinType 连接类型
   * @param <Z> 源实体类型
   * @param <X> 目标实体类型
   * @return 连接对象
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
   * 获取或创建内连接（INNER JOIN）。
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
   * 获取或创建左外连接（LEFT JOIN）。
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
   * 获取或创建右外连接（RIGHT JOIN）。
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
   * 构造用于 LIKE 查询的模糊匹配字符串，格式为 %property%。
   *
   * @param property 原始属性值
   * @return 包裹 % 后的字符串
   */
  static String like(String property) {
    return SymbolConsts.PERCENT + property + SymbolConsts.PERCENT;
  }
}
