package tutorials4j.framework.feature.schedule.web;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.entity.BaseVO;
import tutorials4j.framework.feature.schedule.domain.JobEntity;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JobVO extends BaseVO {
  private String taskCode;
  private String classSimpleName;
  private String cron;
  private String description;
  private Map<String, String> metadata;
  private Duration initialDelay;
  private Integer maxFailureCount;
  private Integer maxExecutionCount;
  private Instant dueDate;

  public static JobVO of(JobEntity entity) {
    JobVO resultVO = new JobVO();
    BeanUtils.copyProperties(entity, resultVO);
    return resultVO;
  }
}
