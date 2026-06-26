package tutorials4j.framework.common.core.exception;

import tutorials4j.framework.common.core.exception.feedback.Feedback;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface ErrorCode {
  Feedback getFeedback();

  default BaseRuntimeException throwed() {
    return new BaseRuntimeException(this);
  }

  default BaseRuntimeException throwed(String message) {
    return new BaseRuntimeException(this, message);
  }

  default BaseRuntimeException throwed(String message, Throwable cause) {
    return new BaseRuntimeException(this, message, cause);
  }

  default BaseRuntimeException throwed(Throwable cause) {
    return new BaseRuntimeException(this, cause);
  }
}
