package tutorials4j.framework.feature.schedule.web;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.Data;

/**
 * 更新定时任务请求 DTO。
 *
 * <p>用于接收前端提交的定时任务更新信息，除任务编码外，可更新任务的调度与执行相关配置。
 *
 * @author Yun Jiao
 */
@Data
public class JobUpdateDTO {
  /** 任务类简单名称 */
  @NotBlank private String classSimpleName;

  /** cron 表达式 */
  @NotBlank private String cron;

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
}
