package tutorials4j.framework.feature.schedule.domain;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class JobSpecification {
  public static Specification<JobEntity> taskCodeEqual(String taskCode) {
    return (root, query, cb) -> taskCodeEqual(root, taskCode, cb);
  }

  public static Predicate taskCodeEqual(Path<JobEntity> path, String taskCode, CriteriaBuilder cb) {
    if (taskCode == null) {
      return cb.conjunction();
    }
    return cb.equal(path.get("taskCode"), taskCode);
  }

  public static Specification<JobEntity> classSimpleNameEqual(String classSimpleName) {
    return (root, query, cb) -> classSimpleNameEqual(root, classSimpleName, cb);
  }

  public static Predicate classSimpleNameEqual(
      Path<JobEntity> path, String classSimpleName, CriteriaBuilder cb) {
    if (classSimpleName == null) {
      return cb.conjunction();
    }
    return cb.equal(path.get("classSimpleName"), classSimpleName);
  }

  public static Specification<JobEntity> descriptionLike(String description) {
    return (root, query, cb) -> descriptionLike(root, description, cb);
  }

  public static Predicate descriptionLike(
      Path<JobEntity> path, String description, CriteriaBuilder cb) {
    if (description == null) {
      return cb.conjunction();
    }
    return cb.like(path.get("description"), like(description));
  }
}
