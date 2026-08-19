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
 * 定时任务视图对象。
 *
 * <p>用于向调用方展示定时任务的完整信息，包含调度配置、执行限制与元数据等。
 *
 * @author Yun Jiao
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class JobVO extends BaseVO {
  /** 任务编码 */
  private String taskCode;

  /** 任务类简单名称 */
  private String classSimpleName;

  /** cron 表达式 */
  private String cron;

  /** 任务描述 */
  private String description;

  /** 任务元数据 */
  private Map<String, String> metadata;

  /** 初始延迟时间 */
  private Duration initialDelay;

  /** 最大失败次数 */
  private Integer maxFailureCount;

  /** 最大执行次数 */
  private Integer maxExecutionCount;

  /** 到期时间 */
  private Instant dueDate;

  /**
   * 将定时任务实体转换为视图对象。
   *
   * @param entity 定时任务实体
   * @return 定时任务视图对象
   */
  public static JobVO of(JobEntity entity) {
    JobVO resultVO = new JobVO();
    BeanUtils.copyProperties(entity, resultVO);
    return resultVO;
  }
}
