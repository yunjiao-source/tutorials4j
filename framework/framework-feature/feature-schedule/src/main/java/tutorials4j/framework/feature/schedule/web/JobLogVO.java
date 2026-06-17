package tutorials4j.framework.feature.schedule.web;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.entity.BaseVO;
import tutorials4j.framework.common.core.entity.YesNoEnum;
import tutorials4j.framework.feature.schedule.domain.JobLogEntity;
import tutorials4j.framework.schedule.core.bean.TaskStatusEnum;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JobLogVO extends BaseVO {
  private TaskStatusEnum taskStatus;
  private String lotNo;
  private Integer totalCount;
  private Integer totalFailureCount;
  private Instant startTime;
  private Instant endTime;
  private YesNoEnum hasError;
  private String message;
  private Instant createdAt;

  public static JobLogVO of(JobLogEntity entity) {
    JobLogVO resultVO = new JobLogVO();
    BeanUtils.copyProperties(entity, resultVO);
    return resultVO;
  }
}
