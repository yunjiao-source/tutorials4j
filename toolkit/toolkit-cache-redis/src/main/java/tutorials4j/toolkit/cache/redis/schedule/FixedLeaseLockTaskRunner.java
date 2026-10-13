package tutorials4j.toolkit.cache.redis.schedule;

import java.time.Duration;
import java.util.Map;
import tutorials4j.toolkit.cache.redis.lock.RedisLockService;
import tutorials4j.toolkit.core.constant.ToolkitConsts;
import tutorials4j.toolkit.core.function.Lockable;
import tutorials4j.toolkit.core.function.TaskRunner;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface FixedLeaseLockTaskRunner extends TaskRunner, Lockable {
  @Override
  default void run(Map<String, String> params) {
    try {
      RedisLockService.instance
          .fixedLease()
          .doInLock(
              ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX + prefix() + key(params),
              expireTime(),
              () -> doRun(params));
    } catch (Exception e) {
      handleException(e);
    }
  }

  void doRun(Map<String, String> params);

  @Override
  default Duration waitTime() {
    // 不需要设置
    return null;
  }
}
