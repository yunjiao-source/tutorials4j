package tutorials4j.framework.captcha.exception;

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
public enum CaptchaErrorCode implements ErrorCode {
  CAPTCHA_CATEGORY_NOT_EXISTS(new NotAcceptableFeedback("验证码分类不存在")),
  CAPTCHA_SERVICE_NOT_EXISTS(new NotAcceptableFeedback("验证码服务不存在")),
  CAPTCHA_GENERATE_FAILURE(new NotAcceptableFeedback("验证码生成失败")),
  CAPTCHA_HAS_EXPIRED(new NotAcceptableFeedback("验证码已过期")),
  CAPTCHA_PARAMETERS_INCOMPLETE(new NotAcceptableFeedback("验证码参数不完整")),
  CAPTCHA_VERIFY_FAILURE(new NotAcceptableFeedback("验证码校验失败")),
  CAPTCHA_AUTH_FAILURE(new NotAcceptableFeedback("验证码认证失败")),
  ;

  private final Feedback feedback;

  CaptchaErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
