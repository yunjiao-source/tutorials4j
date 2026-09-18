package tutorials4j.feature.oauth.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum OAuthFeatureErrorCode implements ErrorCode {
  OPEN_ID_CREATE_FAIL("openid创建异常"),
  UNION_ID_CREATE_FAIL("unionid创建异常"),
  ;

  private final String message;

  OAuthFeatureErrorCode(String message) {
    this.message = message;
  }

  @Override
  public String getCode() {
    return name();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
