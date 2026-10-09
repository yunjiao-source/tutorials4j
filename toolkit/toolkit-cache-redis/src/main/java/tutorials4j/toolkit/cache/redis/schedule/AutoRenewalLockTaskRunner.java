package tutorials4j.toolkit.cache.redis.schedule;

import java.time.Duration;
import java.util.Map;
import tutorials4j.toolkit.cache.redis.lock.RedisLockService;
import tutorials4j.toolkit.core.function.Lockable;
import tutorials4j.toolkit.core.function.TaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface AutoRenewalLockTaskRunner extends TaskRunner, Lockable {
  @Override
  default void run(Map<String, String> params) {
    try {
      RedisLockService.instance.autoRenewal().doInLock(prefix() + key(params), () -> doRun(params));
    } catch (Exception e) {
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
