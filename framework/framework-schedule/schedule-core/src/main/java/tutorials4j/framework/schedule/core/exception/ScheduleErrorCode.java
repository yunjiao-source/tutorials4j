package tutorials4j.framework.schedule.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum ScheduleErrorCode implements ErrorCode {
  SCHEDULE_JOB_IS_RUNNING("任务运行中"),
  SCHEDULE_JOB_BEAN_NOT_EXIST("任务Bean不存在");

  private final Feedback feedback;

  ScheduleErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
