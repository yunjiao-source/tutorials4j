package tutorials4j.framework.schedule.redisson;

import java.util.Map;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

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
          .doInLock(key(), waitTime(), waitTime(), () -> doRun(params));
    } catch (Exception e) {
      handleException(e);
    }
  }

  void doRun(Map<String, String> params);
}
