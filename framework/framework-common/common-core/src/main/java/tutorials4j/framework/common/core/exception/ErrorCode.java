package tutorials4j.framework.common.core.exception;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface ErrorCode {
  Feedback getFeedback();

  default ErrorCodeException throwed() {
    return new ErrorCodeException(this);
  }

  default ErrorCodeException throwed(String message) {
    return new ErrorCodeException(this, message);
  }

  default ErrorCodeException throwed(String message, Throwable cause) {
    return new ErrorCodeException(this, message, cause);
  }

  default ErrorCodeException throwed(Throwable cause) {
    return new ErrorCodeException(this, cause);
  }
}
