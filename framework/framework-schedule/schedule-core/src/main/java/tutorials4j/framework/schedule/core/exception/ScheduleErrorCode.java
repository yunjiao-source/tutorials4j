package tutorials4j.framework.schedule.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 调度模块错误码枚举。
 *
 * <p>定义调度任务执行过程中可能出现的错误，例如任务运行中或任务 Bean 不存在等。
 *
 * @author Yun Jiao
 */
@Getter
public enum ScheduleErrorCode implements ErrorCode {
  /** 任务运行中 */
  SCHEDULE_JOB_IS_RUNNING("任务运行中"),
  /** 任务 Bean 不存在 */
  SCHEDULE_JOB_BEAN_NOT_EXIST("任务Bean不存在");

  private final Feedback feedback;

  /**
   * 构造错误码，基于错误码名称与提示消息构建反馈信息。
   *
   * @param message 错误提示消息
   */
  ScheduleErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
