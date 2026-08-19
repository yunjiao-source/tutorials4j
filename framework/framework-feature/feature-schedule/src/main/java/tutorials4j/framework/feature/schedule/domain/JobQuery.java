package tutorials4j.framework.feature.schedule.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

/**
 * 任务查询条件。
 *
 * <p>封装任务列表查询的过滤条件，并构建对应的查询 Specification。
 *
 * @author Yun Jiao
 */
@Data
public class JobQuery {
  private String taskCode;
  private String classSimpleName;
  private String description;

  /**
   * 构建任务查询条件。
   *
   * @return 组合后的查询 Specification
   */
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
