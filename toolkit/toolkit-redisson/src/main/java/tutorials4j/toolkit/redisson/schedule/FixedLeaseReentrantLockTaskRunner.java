package tutorials4j.toolkit.redisson.schedule;

import java.util.Map;
import tutorials4j.toolkit.core.function.Lockable;
import tutorials4j.toolkit.core.function.TaskRunner;
import tutorials4j.toolkit.redisson.lock.RedissonReentrantLockService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface FixedLeaseReentrantLockTaskRunner extends TaskRunner, Lockable {
  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonReentrantLockService.instance
          .fixedLease()
          .doInLock(prefix() + key(params), waitTime(), expireTime(), () -> doRun(params));
    } catch (Exception e) {
      handleException(e);
    }
  }

  void doRun(Map<String, String> params);
}
