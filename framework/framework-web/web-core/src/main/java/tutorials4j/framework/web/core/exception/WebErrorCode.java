package tutorials4j.framework.web.core.exception;

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
public enum WebErrorCode implements ErrorCode {
  WEB_CLIENT_FAILURE(new NotAcceptableFeedback("调用客户端失败")),
  WEB_ACCESS_LIMITED(new NotAcceptableFeedback("访问被限制")),
  WEB_IDEMPOTENT_FAILURE(new NotAcceptableFeedback("幂等校验")),
  WEB_SIGNATURE_GENERATE_FAILURE(new NotAcceptableFeedback("签名生成失败")),
  WEB_SIGNATURE_PARAMETERS_INCOMPLETE(new NotAcceptableFeedback("签名参数不完整")),
  WEB_SIGNATURE_EXPIRED(new NotAcceptableFeedback("签名已过期")),
  WEB_SIGNATURE_DUPLICATE_REQUEST(new NotAcceptableFeedback("签名重复请求")),
  WEB_SIGNATURE_SECRET_NOT_EXIST(new NotAcceptableFeedback("签名密钥不存在")),
  WEB_SIGNATURE_VERIFY_FAILURE(new NotAcceptableFeedback("签名校验失败")),
  WEB_TOTP_PARAMETERS_INCOMPLETE(new NotAcceptableFeedback("TOTP参数不完整")),
  WEB_TOTP_VERIFY_FAILURE(new NotAcceptableFeedback("TOTP校验失败")),
  WEB_TOTP_AUTH_FAILURE(new NotAcceptableFeedback("TOTP认证失败")),
  WEB_TOTP_SECRET_NOT_EXIST(new NotAcceptableFeedback("TOTP密钥不存在")),
  ;

  private final Feedback feedback;

  WebErrorCode(Feedback feedback) {
    this.feedback = feedback;
    feedback.setCode(this.name());
  }
}
