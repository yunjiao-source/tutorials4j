package tutorials4j.feature.oauth.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum OAuthFeatureErrorCode implements ErrorCode {
  CLIENT_NOT_FOUND("客户端不存在"),
  OPEN_ID_CREATE_FAIL("openid创建时数据校验异常"),
  UNION_ID_CREATE_FAIL("unionid创建时数据校验异常"),
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
