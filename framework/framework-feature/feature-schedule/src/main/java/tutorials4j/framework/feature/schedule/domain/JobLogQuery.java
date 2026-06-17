package tutorials4j.framework.feature.schedule.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class JobLogQuery {
  private Long jobId;
  private TaskStatusEnum taskStatus;
  private YesNoEnum hasError;
  private String errorMessage;
  private Instant startCreatedAt;
  private Instant endCreatedAt;

  public Specification<JobLogEntity> buildSpecification() {
    List<Specification<JobLogEntity>> specList = new ArrayList<>();
    specList.add(JobLogSpecification.jobIdEqual(jobId));
    specList.add(JobLogSpecification.taskStatusEqual(taskStatus));
    specList.add(JobLogSpecification.hasErrorEqual(hasError));
    specList.add(JobLogSpecification.errorMessageLike(errorMessage));
    specList.add(JobLogSpecification.createdAtGte(startCreatedAt));
    specList.add(JobLogSpecification.createdAtLte(endCreatedAt));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
