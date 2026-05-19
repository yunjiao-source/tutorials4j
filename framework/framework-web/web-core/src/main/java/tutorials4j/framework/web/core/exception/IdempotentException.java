package tutorials4j.framework.web.core.exception;

import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * 幂等异常
 *
 * @author Yun Jiao
 */
public class IdempotentException extends FrameworkRuntimeException {
  public IdempotentException() {
    super();
  }

  public IdempotentException(Throwable cause) {
    super(cause);
  }
}
