package tutorials4j.framework.cache.core.exception;

import java.time.Duration;
import org.apache.commons.lang3.exception.ExceptionContext;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LockException extends CacheFrameworkException {

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

  protected void addWaitTime(Duration waitTime) {
    addContextValue("waitTime", waitTime);
  }
}
