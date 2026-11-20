package tutorials4j.service.ai.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public enum AiServiceErrorCode implements ErrorCode {
  SESSION_NOT_FOUND("AI会话不存在"),
  SESSION_ACCESS_DENIED("AI会话访问被限制"),
  ;

  private final String message;

  AiServiceErrorCode(String message) {
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
