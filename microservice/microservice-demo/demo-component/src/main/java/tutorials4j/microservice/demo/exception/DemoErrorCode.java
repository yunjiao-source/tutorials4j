package tutorials4j.microservice.demo.exception;

import tutorials4j.toolkit.core.exception.ErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum DemoErrorCode implements ErrorCode {
  DEMO_FAIL("示例失败"),
  ;

  private final String message;

  DemoErrorCode(String message) {
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
