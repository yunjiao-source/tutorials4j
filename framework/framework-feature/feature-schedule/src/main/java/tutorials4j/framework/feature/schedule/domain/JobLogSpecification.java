package tutorials4j.framework.feature.schedule.domain;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.framework.common.core.bean.YesNoEnum;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;

/**
 * 任务日志查询条件（Specification）构建工具。
 *
 * <p>提供按任务、批次号、状态、错误标记、消息及创建时间等条件构造查询断言的方法。
 *
 * @author Yun Jiao
 */
public class JobLogSpecification {
  /**
   * 构建任务编码精确匹配的查询条件。
   *
   * @param taskCode 任务编码
   * @return 任务编码查询条件
   */
  public static Specification<JobLogEntity> taskCodeEqual(String taskCode) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(taskCode)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("job").get("taskCode"), taskCode);
    };
  }

  /**
   * 构建执行类名精确匹配的查询条件。
   *
   * @param classSimpleName 执行类简单类名
   * @return 类名查询条件
   */
  public static Specification<JobLogEntity> classSimpleNameEqual(String classSimpleName) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(classSimpleName)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("job").get("classSimpleName"), classSimpleName);
    };
  }

  /**
   * 构建任务主键精确匹配的查询条件。
   *
   * @param jobId 任务主键
   * @return 任务主键查询条件
   */
  public static Specification<JobLogEntity> jobIdEqual(Long jobId) {
    return (root, query, cb) -> {
      if (jobId == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("job").get("id"), jobId);
    };
  }

  /**
   * 构建批次号精确匹配的查询条件。
   *
   * @param lotNo 批次号
   * @return 批次号查询条件
   */
  public static Specification<JobLogEntity> lotNoEqual(String lotNo) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(lotNo)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("lotNo"), lotNo);
    };
  }

  /**
   * 构建任务状态精确匹配的查询条件。
   *
   * @param taskStatus 任务状态
   * @return 任务状态查询条件
   */
  public static Specification<JobLogEntity> taskStatusEqual(TaskStatusEnum taskStatus) {
    return (root, query, cb) -> {
      if (taskStatus == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("taskStatus"), taskStatus);
    };
  }

  /**
   * 构建错误标记精确匹配的查询条件。
   *
   * @param hasError 是否发生错误
   * @return 错误标记查询条件
   */
  public static Specification<JobLogEntity> hasErrorEqual(YesNoEnum hasError) {
    return (root, query, cb) -> {
      if (hasError == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("hasError"), hasError);
    };
  }

  /**
   * 构建日志消息模糊匹配的查询条件。
   *
   * @param message 消息关键字
   * @return 消息模糊查询条件
   */
  public static Specification<JobLogEntity> messageLike(String message) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(message)) {
        return cb.conjunction();
      }
      return cb.like(root.get("message"), like(message));
    };
  }

  /**
   * 构建创建时间大于等于指定时间的查询条件。
   *
   * @param startCreatedAt 起始创建时间
   * @return 创建时间查询条件
   */
  public static Specification<JobLogEntity> createdAtGte(Instant startCreatedAt) {
    return (root, query, cb) -> {
      if (startCreatedAt == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("createdAt"), startCreatedAt);
    };
  }

  /**
   * 构建创建时间小于等于指定时间的查询条件。
   *
   * @param endCreatedAt 截止创建时间
   * @return 创建时间查询条件
   */
  public static Specification<JobLogEntity> createdAtLte(Instant endCreatedAt) {
    return (root, query, cb) -> {
      if (endCreatedAt == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("createdAt"), endCreatedAt);
    };
  }
}
