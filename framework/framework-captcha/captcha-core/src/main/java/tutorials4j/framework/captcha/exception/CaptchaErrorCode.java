package tutorials4j.framework.captcha.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * 验证码模块错误码枚举。
 *
 * <p>定义验证码生成、校验与认证过程中可能出现的错误，例如验证码分类或服务不存在、验证码过期、参数不完整、校验或认证失败等。
 *
 * @author Yun Jiao
 */
@Getter
public enum CaptchaErrorCode implements ErrorCode {
  /** 验证码分类不存在 */
  CAPTCHA_CATEGORY_NOT_EXISTS("验证码分类不存在"),
  /** 验证码服务不存在 */
  CAPTCHA_SERVICE_NOT_EXISTS("验证码服务不存在"),
  /** 验证码生成失败 */
  CAPTCHA_GENERATE_FAILURE("验证码生成失败"),
  /** 验证码已过期 */
  CAPTCHA_HAS_EXPIRED("验证码已过期"),
  /** 验证码参数不完整 */
  CAPTCHA_PARAMETERS_INCOMPLETE("验证码参数不完整"),
  /** 验证码校验失败 */
  CAPTCHA_VERIFY_FAILURE("验证码校验失败"),
  /** 验证码认证失败 */
  CAPTCHA_AUTH_FAILURE("验证码认证失败"),
  ;

  private final Feedback feedback;

  /**
   * 构造错误码，基于错误码名称与提示消息构建反馈信息。
   *
   * @param message 错误提示消息
   */
  CaptchaErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
