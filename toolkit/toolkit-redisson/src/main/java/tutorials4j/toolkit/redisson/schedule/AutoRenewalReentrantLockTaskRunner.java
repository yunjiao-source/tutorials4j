package tutorials4j.toolkit.redisson.schedule;

import java.time.Duration;
import java.util.Map;
import tutorials4j.toolkit.core.constant.ToolkitConsts;
import tutorials4j.toolkit.core.function.Lockable;
import tutorials4j.toolkit.core.function.TaskRunner;
import tutorials4j.toolkit.redisson.lock.RedissonReentrantLockService;

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
          .doInLock(
              ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + prefix() + key(params),
              waitTime(),
              () -> doRun(params));
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
