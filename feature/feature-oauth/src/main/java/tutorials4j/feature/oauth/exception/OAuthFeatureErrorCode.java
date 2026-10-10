package tutorials4j.feature.oauth.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum OAuthFeatureErrorCode implements ErrorCode {
  TOO_MANY_LOGIN_FAIL("频繁登录失败，请稍后重试"),
  USER_VALIDATE_FAIL("用户信息校验失败"),
  ACCOUNT_NOT_FOUND("账户不存在"),

  LOGIN_FAIL("登录失败"),
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
