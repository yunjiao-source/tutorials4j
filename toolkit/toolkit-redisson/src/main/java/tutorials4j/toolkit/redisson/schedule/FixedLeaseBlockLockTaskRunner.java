package tutorials4j.toolkit.redisson.schedule;

import java.time.Duration;
import java.util.Map;
import tutorials4j.toolkit.core.constant.ToolkitConsts;
import tutorials4j.toolkit.core.function.Lockable;
import tutorials4j.toolkit.core.function.TaskRunner;
import tutorials4j.toolkit.redisson.lock.RedissonBlockLockService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface FixedLeaseBlockLockTaskRunner extends TaskRunner, Lockable {
  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonBlockLockService.instance
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
