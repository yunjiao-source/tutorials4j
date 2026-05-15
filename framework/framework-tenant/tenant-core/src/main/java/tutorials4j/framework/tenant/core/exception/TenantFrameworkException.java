package tutorials4j.framework.tenant.core.exception;

import org.apache.commons.lang3.exception.ExceptionContext;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class TenantFrameworkException extends FrameworkRuntimeException {
  public TenantFrameworkException() {}

  public TenantFrameworkException(String message) {
    super(message);
  }

  public TenantFrameworkException(String message, Throwable cause) {
    super(message, cause);
  }

  public TenantFrameworkException(String message, Throwable cause, ExceptionContext context) {
    super(message, cause, context);
  }

  public TenantFrameworkException(Throwable cause) {
    super(cause);
  }
}
