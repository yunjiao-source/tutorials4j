package tutorials4j.framework.web.core.exception;

import org.apache.commons.lang3.exception.ExceptionContext;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * web客户端异常
 *
 * @author Yun Jiao
 */
public class WebFrameworkException extends FrameworkRuntimeException {
  public WebFrameworkException() {}

  public WebFrameworkException(String message) {
    super(message);
  }

  public WebFrameworkException(String message, Throwable cause) {
    super(message, cause);
  }

  public WebFrameworkException(String message, Throwable cause, ExceptionContext context) {
    super(message, cause, context);
  }

  public WebFrameworkException(Throwable cause) {
    super(cause);
  }
}
