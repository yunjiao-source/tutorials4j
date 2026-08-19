package tutorials4j.framework.feature.schedule.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import tutorials4j.framework.common.core.bean.YesNoEnum;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;

/**
 * 任务日志查询条件。
 *
 * <p>封装任务日志列表查询的各项过滤条件，并构建对应的查询 Specification。
 *
 * @author Yun Jiao
 */
@Data
public class JobLogQuery {
  private Long jobId;
  private TaskStatusEnum taskStatus;
  private YesNoEnum hasError;
  private String lotNo;
  private String message;
  private Instant startCreatedAt;
  private Instant endCreatedAt;

  private String taskCode;
  private String classSimpleName;

  /**
   * 构建任务日志查询条件。
   *
   * @return 组合后的查询 Specification
   */
  public Specification<JobLogEntity> buildSpecification() {
    List<Specification<JobLogEntity>> specList = new ArrayList<>();
    specList.add(JobLogSpecification.jobIdEqual(jobId));
    specList.add(JobLogSpecification.lotNoEqual(lotNo));
    specList.add(JobLogSpecification.taskStatusEqual(taskStatus));
    specList.add(JobLogSpecification.hasErrorEqual(hasError));
    specList.add(JobLogSpecification.messageLike(message));
    specList.add(JobLogSpecification.createdAtGte(startCreatedAt));
    specList.add(JobLogSpecification.createdAtLte(endCreatedAt));

    specList.add(JobLogSpecification.taskCodeEqual(taskCode));
    specList.add(JobLogSpecification.classSimpleNameEqual(classSimpleName));

    return specList.stream()
        .filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse((root, query, cb) -> cb.conjunction());
  }
}
