package tutorials4j.framework.message.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum MessageErrorCode implements ErrorCode {
  MESSAGE_TYPE_MISMATCH("消息类型不匹配"),
  MESSAGE_KEY_MISMATCH("消息键不匹配"),
  MESSAGE_KEY_NOT_CONFIG("消息键没有配置信息"),
  ;

  private final Feedback feedback;

  MessageErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
