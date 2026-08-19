package tutorials4j.framework.web.core.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * Web 模块错误码枚举，定义 Web 层各类异常的错误码与反馈信息。
 *
 * @author Yun Jiao
 */
@Getter
public enum WebErrorCode implements ErrorCode {
  /** 调用客户端失败。 */
  WEB_CLIENT_FAILURE("调用客户端失败"),
  /** 访问被限制。 */
  WEB_ACCESS_LIMITED("访问被限制"),
  /** 幂等校验失败。 */
  WEB_IDEMPOTENT_FAILURE("幂等校验失败"),
  /** 签名生成失败。 */
  WEB_SIGNATURE_GENERATE_FAILURE("签名生成失败"),
  /** 签名参数不完整。 */
  WEB_SIGNATURE_PARAMETERS_INCOMPLETE("签名参数不完整"),
  /** 签名已过期。 */
  WEB_SIGNATURE_EXPIRED("签名已过期"),
  /** 签名重复请求。 */
  WEB_SIGNATURE_DUPLICATE_REQUEST("签名重复请求"),
  /** 签名密钥不存在。 */
  WEB_SIGNATURE_SECRET_NOT_EXIST("签名密钥不存在"),
  /** 签名校验失败。 */
  WEB_SIGNATURE_VERIFY_FAILURE("签名校验失败"),
  /** TOTP 参数不完整。 */
  WEB_TOTP_PARAMETERS_INCOMPLETE("TOTP参数不完整"),
  /** TOTP 校验失败。 */
  WEB_TOTP_VERIFY_FAILURE("TOTP校验失败"),
  /** TOTP 认证失败。 */
  WEB_TOTP_AUTH_FAILURE("TOTP认证失败"),
  /** TOTP 密钥不存在。 */
  WEB_TOTP_SECRET_NOT_EXIST("TOTP密钥不存在"),
  ;

  /** 错误反馈信息。 */
  private final Feedback feedback;

  /**
   * 构造错误码，根据枚举名称与错误消息构建反馈信息。
   *
   * @param message 错误消息
   */
  WebErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
