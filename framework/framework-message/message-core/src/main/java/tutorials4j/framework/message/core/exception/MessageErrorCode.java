package tutorials4j.framework.message.core.exception;

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
public enum MessageErrorCode implements ErrorCode {
  MESSAGE_KEY_MISMATCH(new NotAcceptableFeedback("消息与消息键不匹配")),
  MESSAGE_KEY_NOT_CONFIG(new NotAcceptableFeedback("消息键没有配置信息")),
  ;

  private final Feedback feedback;

  MessageErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
