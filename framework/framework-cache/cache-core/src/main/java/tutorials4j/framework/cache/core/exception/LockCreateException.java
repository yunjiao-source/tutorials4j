package tutorials4j.framework.cache.core.exception;

import java.time.Duration;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class LockCreateException extends FrameworkRuntimeException {

  public LockCreateException() {
    super("创建锁失败");
  }

  public LockCreateException(String lockKey, Throwable throwable) {
    super("创建锁失败", throwable);
  }

  public LockCreateException(String lockKey, Duration waitTime) {
    this();
    addLockKey(lockKey);
    addWaitTime(waitTime);
  }

  public LockCreateException(String lockKey) {
    this();
    addLockKey(lockKey);
  }

  protected void addLockKey(String lockKey) {
    addContextValue("lockKey", lockKey);
  }

  protected void addWaitTime(Duration waitTime) {
    addContextValue("waitTime", waitTime);
  }
}
