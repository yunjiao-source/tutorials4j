package tutorials4j.framework.captcha.exception;

import org.apache.commons.lang3.exception.ExceptionContext;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class CaptchaException extends FrameworkRuntimeException {

  public CaptchaException() {}

  public CaptchaException(String message) {
    super(message);
  }

  public CaptchaException(String message, Throwable cause) {
    super(message, cause);
  }

  public CaptchaException(String message, Throwable cause, ExceptionContext context) {
    super(message, cause, context);
  }

  public CaptchaException(Throwable cause) {
    super(cause);
  }
}
