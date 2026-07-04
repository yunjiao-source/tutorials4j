package tutorials4j.framework.schedule.redisson;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface AutoRenewalReentrantLockTaskRunner extends TaskRunner, Lockable {

  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonReentrantLockService.instance
          .autoRenewal()
          .doInLock(key(), waitTime(), () -> doRun(params));
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
}
