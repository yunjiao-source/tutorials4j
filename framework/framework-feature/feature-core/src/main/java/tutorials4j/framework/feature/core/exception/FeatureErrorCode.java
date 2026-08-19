package tutorials4j.framework.feature.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 功能模块错误码枚举。
 *
 * <p>定义功能模块相关的错误码，目前暂未定义具体错误码，作为功能模块错误码的统一入口。
 *
 * @author Yun Jiao
 */
@Getter
public enum FeatureErrorCode implements ErrorCode {
  ;

  private final Feedback feedback;

  /**
   * 构造错误码，基于错误码名称与提示消息构建反馈信息。
   *
   * @param message 错误提示消息
   */
  FeatureErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
