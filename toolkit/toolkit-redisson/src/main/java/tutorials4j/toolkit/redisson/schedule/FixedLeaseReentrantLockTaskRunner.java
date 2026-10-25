package tutorials4j.toolkit.redisson.schedule;

import java.util.Map;
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.core.function.Lockable;
import tutorials4j.toolkit.core.function.TaskRunner;
import tutorials4j.toolkit.redisson.lock.RedissonReentrantLockService;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
public interface FixedLeaseReentrantLockTaskRunner extends TaskRunner, Lockable {
  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonReentrantLockService.instance
          .fixedLease()
          .doInLock(
              DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + prefix() + key(params),
              waitTime(),
              expireTime(),
              () -> doRun(params));
    } catch (Exception e) {
      handleException(e);
    }
  }

  void doRun(Map<String, String> params);
}
