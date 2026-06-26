package tutorials4j.framework.common.core.exception;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class BaseException extends Exception {

  public BaseException() {}

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public BaseException(Throwable cause) {
    super(cause);
  }

  public BaseException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
