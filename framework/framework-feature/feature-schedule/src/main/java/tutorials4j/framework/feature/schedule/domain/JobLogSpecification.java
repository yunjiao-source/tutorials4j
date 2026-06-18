package tutorials4j.framework.feature.schedule.domain;

import static tutorials4j.framework.data.core.util.JPAUtils.leftJoin;
import static tutorials4j.framework.data.core.util.JPAUtils.like;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class JobLogSpecification {
  public static Specification<JobLogEntity> taskCodeEqual(String taskCode) {
    return (root, query, cb) -> {
      Join<JobLogEntity, JobEntity> jobJoin = leftJoin(root, "job");
      return JobSpecification.taskCodeEqual(jobJoin, taskCode, cb);
    };
  }

  public static Specification<JobLogEntity> classSimpleNameEqual(String classSimpleName) {
    return (root, query, cb) -> {
      Join<JobLogEntity, JobEntity> jobJoin = leftJoin(root, "job");
      return JobSpecification.classSimpleNameEqual(jobJoin, classSimpleName, cb);
    };
  }

  public static Specification<JobLogEntity> jobIdEqual(Long jobId) {
    return (root, query, cb) -> jobIdEqual(root, jobId, cb);
  }

  public static Predicate jobIdEqual(Path<JobLogEntity> path, Long jobId, CriteriaBuilder cb) {
    if (jobId == null) {
      return cb.conjunction();
    }
    return cb.equal(path.get("job").get("id"), jobId);
  }

  public static Specification<JobLogEntity> lotNoEqual(String lotNo) {
    return (root, query, cb) -> lotNoEqual(root, lotNo, cb);
  }

  public static Predicate lotNoEqual(Path<JobLogEntity> path, String lotNo, CriteriaBuilder cb) {
    if (StringUtils.isBlank(lotNo)) {
      return cb.conjunction();
    }
    return cb.equal(path.get("lotNo"), lotNo);
  }

  public static Specification<JobLogEntity> taskStatusEqual(TaskStatusEnum taskStatus) {
    return (root, query, cb) -> taskStatusEqual(root, taskStatus, cb);
  }

  public static Predicate taskStatusEqual(
      Path<JobLogEntity> path, TaskStatusEnum taskStatus, CriteriaBuilder cb) {
    if (taskStatus == null) {
      return cb.conjunction();
    }
    return cb.equal(path.get("taskStatus"), taskStatus);
  }

  public static Specification<JobLogEntity> hasErrorEqual(YesNoEnum hasError) {
    return (root, query, cb) -> hasErrorEqual(root, hasError, cb);
  }

  public static Predicate hasErrorEqual(
      Path<JobLogEntity> path, YesNoEnum hasError, CriteriaBuilder cb) {
    if (hasError == null) {
      return cb.conjunction();
    }
    return cb.equal(path.get("hasError"), hasError);
  }

  public static Specification<JobLogEntity> messageLike(String message) {
    return (root, query, cb) -> errorMessageLike(root, message, cb);
  }

  public static Predicate errorMessageLike(
      Path<JobLogEntity> path, String message, CriteriaBuilder cb) {
    if (StringUtils.isBlank(message)) {
      return cb.conjunction();
    }
    return cb.like(path.get("message"), like(message));
  }

  public static Specification<JobLogEntity> createdAtGte(Instant startCreatedAt) {
    return (root, query, cb) -> createdAtGte(root, startCreatedAt, cb);
  }

  public static Predicate createdAtGte(
      Path<JobLogEntity> path, Instant startCreatedAt, CriteriaBuilder cb) {
    if (startCreatedAt == null) {
      return cb.conjunction();
    }
    return cb.greaterThanOrEqualTo(path.get("createdAt"), startCreatedAt);
  }

  public static Specification<JobLogEntity> createdAtLte(Instant endCreatedAt) {
    return (root, query, cb) -> createdAtLte(root, endCreatedAt, cb);
  }

  public static Predicate createdAtLte(
      Path<JobLogEntity> path, Instant endCreatedAt, CriteriaBuilder cb) {
    if (endCreatedAt == null) {
      return cb.conjunction();
    }
    return cb.lessThanOrEqualTo(path.get("createdAt"), endCreatedAt);
  }
}
