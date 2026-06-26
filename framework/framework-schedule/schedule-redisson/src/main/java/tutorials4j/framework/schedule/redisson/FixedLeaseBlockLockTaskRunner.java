package tutorials4j.framework.schedule.redisson;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.exception.CacheErrorCode;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockService;
import tutorials4j.framework.common.core.exception.BaseRuntimeException;
import tutorials4j.framework.common.core.exception.ErrorCode;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface FixedLeaseBlockLockTaskRunner extends TaskRunner, Lockable {

  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonBlockLockService.instance
          .fixedLease()
          .doInLock(key(), expireTime(), () -> doRun(params));
    } catch (BaseRuntimeException e) {
      ErrorCode errorCode = e.getErrorCode();
      if (errorCode instanceof CacheErrorCode) {
        handleException(e);
      }
      throw e;
    }
  }

  void doRun(Map<String, String> params);

  @Override
  default Duration waitTime() {
    // 不需要设置
    return null;
  }
}
