package tutorials4j.framework.cache.core.exception;

import org.apache.commons.lang3.exception.ExceptionContext;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LockException extends FrameworkRuntimeException {

  public LockException() {}

  public LockException(String message) {
    super(message);
  }

  public LockException(String message, Throwable cause) {
    super(message, cause);
  }

  public LockException(String message, Throwable cause, ExceptionContext context) {
    super(message, cause, context);
  }

  public LockException(Throwable cause) {
    super(cause);
  }

  public LockException(Throwable cause, String lockKey) {
    super(cause);
    addLockKey(lockKey);
  }

  protected void addLockKey(String lockKey) {
    addContextValue("lockKey", lockKey);
  }
}
