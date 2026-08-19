package tutorials4j.framework.message.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 消息模块错误码枚举。
 *
 * <p>定义消息处理过程中可能出现的错误，例如消息类型不匹配、消息键不匹配、消息键未配置等。
 *
 * @author Yun Jiao
 */
@Getter
public enum MessageErrorCode implements ErrorCode {
  /** 消息类型不匹配 */
  MESSAGE_TYPE_MISMATCH("消息类型不匹配"),
  /** 消息键不匹配 */
  MESSAGE_KEY_MISMATCH("消息键不匹配"),
  /** 消息键没有配置信息 */
  MESSAGE_KEY_NOT_CONFIG("消息键没有配置信息"),
  ;

  private final Feedback feedback;

  /**
   * 构造错误码，基于错误码名称与提示消息构建反馈信息。
   *
   * @param message 错误提示消息
   */
  MessageErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
