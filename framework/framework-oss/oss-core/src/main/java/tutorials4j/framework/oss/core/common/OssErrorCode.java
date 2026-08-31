package tutorials4j.framework.oss.core.common;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * OSS 模块错误码枚举。
 *
 * <p>定义该模块可能抛出的业务错误码，每个枚举项通过构造器绑定错误反馈信息。
 *
 * @author Yun Jiao
 */
@Getter
public enum OssErrorCode implements ErrorCode {
  ;

  private final Feedback feedback;

  /**
   * 构造错误码，基于错误码名称与提示消息构建反馈信息。
   *
   * @param message 错误提示消息
   */
  OssErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
