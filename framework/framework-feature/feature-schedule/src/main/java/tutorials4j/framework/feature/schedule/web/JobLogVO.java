package tutorials4j.framework.feature.schedule.web;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import tutorials4j.framework.common.core.bean.YesNoEnum;
import tutorials4j.framework.common.core.entity.BaseVO;
import tutorials4j.framework.feature.schedule.domain.JobEntity;
import tutorials4j.framework.feature.schedule.domain.JobLogEntity;
import tutorials4j.framework.schedule.spring.bean.TaskStatusEnum;

/**
 * 任务执行日志视图对象。
 *
 * <p>用于向调用方展示定时任务单次执行的日志信息，包含执行状态、耗时统计与关联任务信息等。
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JobLogVO extends BaseVO {
  /** 任务执行状态 */
  private TaskStatusEnum taskStatus;

  /** 批次号 */
  private String lotNo;

  /** 总执行数量 */
  private Integer totalCount;

  /** 失败执行数量 */
  private Integer totalFailureCount;

  /** 开始时间 */
  private Instant startTime;

  /** 结束时间 */
  private Instant endTime;

  /** 是否发生错误 */
  private YesNoEnum hasError;

  /** 执行消息 */
  private String message;

  /** 创建时间 */
  private Instant createdAt;

  /** 任务编码 */
  private String taskCode;

  /** 任务类简单名称 */
  private String classSimpleName;

  /**
   * 将任务执行日志实体转换为视图对象，并补充关联任务信息。
   *
   * @param entity 任务执行日志实体
   * @return 任务执行日志视图对象
   */
  public static JobLogVO of(JobLogEntity entity) {
    JobLogVO resultVO = new JobLogVO();
    BeanUtils.copyProperties(entity, resultVO);

    JobEntity job = entity.getJob();
    if (job != null) {
      resultVO.setTaskCode(job.getTaskCode());
      resultVO.setClassSimpleName(job.getClassSimpleName());
    }
    return resultVO;
  }
}
