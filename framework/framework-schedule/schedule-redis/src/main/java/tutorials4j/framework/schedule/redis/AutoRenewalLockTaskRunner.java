package tutorials4j.framework.schedule.redis;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.exception.LockException;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redis.lock.RedisLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface AutoRenewalLockTaskRunner extends TaskRunner, Lockable {

  @Override
  default void run(Map<String, String> params) {
    try {
      RedisLockService.instance.autoRenewal().doInLock(key(), () -> doRun(params));
    } catch (LockException e) {
      handleException(e);
    }
  }

  void doRun(Map<String, String> params);

  @Override
  default Duration expireTime() {
    // 不需要设置
    return null;
  }

  @Override
  default Duration waitTime() {
    // 不需要设置
    return null;
  }
}
