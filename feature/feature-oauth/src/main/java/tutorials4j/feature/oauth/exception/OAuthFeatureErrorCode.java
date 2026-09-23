package tutorials4j.feature.oauth.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum OAuthFeatureErrorCode implements ErrorCode {
  USER_VALIDATE_FAIL("用户信息校验失败"),
  ACCOUNT_NOT_FOUND("账户不存在"),
  ACCOUNT_PASSWORD_EXPIRED("账户密码已过期"),
  ACCOUNT_EXPIRED("账户已过期"),
  ACCOUNT_NOT_ACTIVE("登录账户非活动状态"),
  LOGIN_FAIL("登录失败：用户名或密码错误"),
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
