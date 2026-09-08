package tutorials4j.framework.common.core.exception;

import lombok.Getter;

/**
 * 基础错误码枚举，定义框架与业务通用的错误场景。
 *
 * <p>每个常量携带一个人类可读的错误提示信息，并实现 {@link ErrorCode} 接口， 可通过 {@link #getFeedback()} 获取统一的错误反馈对象。
 *
 * @author Yun Jiao
 */
@Getter
public enum BaseErrorCode implements ErrorCode {
  SYSTEM_EXCPEITON("系统异常"),
  TOO_MANY_REQUESTS("太多请求"),
  UNAUTHORIZED("未登录"),
  FORBIDDEN("无权访问"),
  ;

  /** 该错误码对应的错误反馈信息 */
  private final Feedback feedback;

  /**
   * 构造错误码常量。
   *
   * @param message 错误提示信息
   */
  BaseErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
