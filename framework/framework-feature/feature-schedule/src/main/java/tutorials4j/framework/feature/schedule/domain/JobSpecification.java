package tutorials4j.framework.feature.schedule.domain;

import static tutorials4j.framework.data.core.util.JPAUtils.like;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class JobSpecification {
  public static Specification<JobEntity> taskCodeEqual(String taskCode) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(taskCode)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("taskCode"), taskCode);
    };
  }

  public static Specification<JobEntity> classSimpleNameEqual(String classSimpleName) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(classSimpleName)) {
        return cb.conjunction();
      }
      return cb.equal(root.get("classSimpleName"), classSimpleName);
    };
  }

  public static Specification<JobEntity> descriptionLike(String description) {
    return (root, query, cb) -> {
      if (StringUtils.isBlank(description)) {
        return cb.conjunction();
      }
      return cb.like(root.get("description"), like(description));
    };
  }
}
