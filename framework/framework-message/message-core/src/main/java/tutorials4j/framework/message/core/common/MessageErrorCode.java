package tutorials4j.framework.message.core.common;

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
  MESSAGE_HANDLING_FAIL("消息处理失败"),
  MESSAGE_PUBLISH_FAIL("消息发送失败"),
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
