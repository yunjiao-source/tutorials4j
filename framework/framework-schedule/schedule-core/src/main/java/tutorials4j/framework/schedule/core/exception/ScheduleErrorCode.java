package tutorials4j.framework.schedule.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.feedback.Feedback;
import tutorials4j.framework.common.core.exception.feedback.NotAcceptableFeedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum ScheduleErrorCode implements ErrorCode {
  SCHEDULE_JOB_IS_RUNNING(new NotAcceptableFeedback("任务运行中")),
  SCHEDULE_JOB_BEAN_NOT_EXIST(new NotAcceptableFeedback("任务Bean不存在"));

  private final Feedback feedback;

  ScheduleErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
