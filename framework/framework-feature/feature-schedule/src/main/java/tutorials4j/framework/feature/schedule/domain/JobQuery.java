package tutorials4j.framework.feature.schedule.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class JobQuery {
  private String taskCode;
  private String classSimpleName;
  private String description;

  public Specification<JobEntity> buildSpecification() {
    List<Specification<JobEntity>> specList = new ArrayList<>();
    specList.add(JobSpecification.taskCodeEqual(taskCode));
    specList.add(JobSpecification.classSimpleNameEqual(classSimpleName));
    specList.add(JobSpecification.descriptionLike(description));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
