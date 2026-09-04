package tutorials4j.framework.auth.core.common;

import lombok.Getter;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.common.core.exception.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
public enum OAuthErrorCode implements ErrorCode {
  API_KEY_VALUE_NOT_EXIST("缺少 API Key");
  private final Feedback feedback;

  OAuthErrorCode(String message) {
    this.feedback = Feedback.builder().code(this.name()).message(message).build();
  }
}
