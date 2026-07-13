package tutorials4j.framework.captcha.exception;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum CaptchaErrorCode implements ErrorCode {
  CAPTCHA_CATEGORY_NOT_EXISTS("验证码分类不存在"),
  CAPTCHA_SERVICE_NOT_EXISTS("验证码服务不存在"),
  CAPTCHA_GENERATE_FAILURE("验证码生成失败"),
  CAPTCHA_HAS_EXPIRED("验证码已过期"),
  CAPTCHA_PARAMETERS_INCOMPLETE("验证码参数不完整"),
  CAPTCHA_VERIFY_FAILURE("验证码校验失败"),
  CAPTCHA_AUTH_FAILURE("验证码认证失败"),
  ;

  private final Feedback feedback;

  CaptchaErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
