package tutorials4j.framework.feature.schedule.domain;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.framework.common.core.bean.YesNoEnum;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class JobLogSpecification {
  public static Specification<JobLogEntity> taskCodeEqual(String taskCode) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(taskCode)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("job").get("taskCode"), taskCode);
    };
  }

  public static Specification<JobLogEntity> classSimpleNameEqual(String classSimpleName) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(classSimpleName)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("job").get("classSimpleName"), classSimpleName);
    };
  }

  public static Specification<JobLogEntity> jobIdEqual(Long jobId) {
    return (root, query, cb) -> {
      if (jobId == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("job").get("id"), jobId);
    };
  }

  public static Specification<JobLogEntity> lotNoEqual(String lotNo) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(lotNo)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("lotNo"), lotNo);
    };
  }

  public static Specification<JobLogEntity> taskStatusEqual(TaskStatusEnum taskStatus) {
    return (root, query, cb) -> {
      if (taskStatus == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("taskStatus"), taskStatus);
    };
  }

  public static Specification<JobLogEntity> hasErrorEqual(YesNoEnum hasError) {
    return (root, query, cb) -> {
      if (hasError == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("hasError"), hasError);
    };
  }

  public static Specification<JobLogEntity> messageLike(String message) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(message)) {
        return cb.conjunction();
      }
      return cb.like(root.get("message"), like(message));
    };
  }

  public static Specification<JobLogEntity> createdAtGte(Instant startCreatedAt) {
    return (root, query, cb) -> {
      if (startCreatedAt == null) {
        return cb.conjunction();
      }
      return cb.greaterThanOrEqualTo(root.get("createdAt"), startCreatedAt);
    };
  }

  public static Specification<JobLogEntity> createdAtLte(Instant endCreatedAt) {
    return (root, query, cb) -> {
      if (endCreatedAt == null) {
        return cb.conjunction();
      }
      return cb.lessThanOrEqualTo(root.get("createdAt"), endCreatedAt);
    };
  }
}
