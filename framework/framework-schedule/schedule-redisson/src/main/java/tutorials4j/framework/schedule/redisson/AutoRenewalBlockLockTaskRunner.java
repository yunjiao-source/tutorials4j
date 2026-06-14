package tutorials4j.framework.schedule.redisson;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.exception.LockException;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface AutoRenewalBlockLockTaskRunner extends TaskRunner, Lockable {

  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonBlockLockService.instance.autoRenewal().doInLock(key(), () -> doRun(params));
    } catch (LockException e) {
      handleException(e);
    }
  }

  void doRun(Map<String, String> params);

  @Override
  default Duration waitTime() {
    // 不需要设置
    return null;
  }

  @Override
  default Duration expireTime() {
    // 不需要设置
    return null;
  }
}
