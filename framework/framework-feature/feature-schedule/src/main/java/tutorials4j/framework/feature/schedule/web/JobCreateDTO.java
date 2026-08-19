package tutorials4j.framework.feature.schedule.web;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建定时任务请求 DTO。
 *
 * <p>用于接收前端提交的新增定时任务信息，其中任务编码、任务类简单名称与 cron 表达式为必填项。
 *
 * @author Yun Jiao
 */
@Data
public class JobCreateDTO {
  /** 任务编码 */
  @NotBlank private String taskCode;

  /** 任务类简单名称 */
  @NotBlank private String classSimpleName;

  /** cron 表达式 */
  @NotBlank private String cron;

  /** 任务描述 */
  private String description;
}
