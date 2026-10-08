package tutorials4j.toolkit.core.exception;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public enum CommonErrorCode implements ErrorCode {
  ;

  private final String message;

  CommonErrorCode(String message) {
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
