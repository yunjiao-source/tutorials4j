package tutorials4j.framework.feature.schedule.domain;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * 任务查询条件（Specification）构建工具。
 *
 * <p>提供按任务编码、执行类名及描述等条件构造查询断言的方法。
 *
 * @author Yun Jiao
 */
public class JobSpecification {
  /**
   * 构建任务编码精确匹配的查询条件。
   *
   * @param taskCode 任务编码
   * @return 任务编码查询条件
   */
  public static Specification<JobEntity> taskCodeEqual(String taskCode) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(taskCode)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("taskCode"), taskCode);
    };
  }

  /**
   * 构建执行类名精确匹配的查询条件。
   *
   * @param classSimpleName 执行类简单类名
   * @return 类名查询条件
   */
  public static Specification<JobEntity> classSimpleNameEqual(String classSimpleName) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(classSimpleName)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("classSimpleName"), classSimpleName);
    };
  }

  /**
   * 构建任务描述模糊匹配的查询条件。
   *
   * @param description 描述关键字
   * @return 描述模糊查询条件
   */
  public static Specification<JobEntity> descriptionLike(String description) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(description)) {
        return cb.conjunction();
      }
      return cb.like(root.get("description"), like(description));
    };
  }
}
