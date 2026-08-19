package tutorials4j.framework.schedule.redisson;

import java.util.Map;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 使用固定租期可重入锁执行任务的调度任务接口。
 *
 * <p>任务执行时通过 {@link RedissonReentrantLockService} 的固定租期模式加锁，锁在指定等待与过期时间后释放， 适用于可以预估执行耗时的任务。
 *
 * @author Yun Jiao
 */
public interface FixedLeaseReentrantLockTaskRunner extends TaskRunner, Lockable {

  /**
   * 在固定租期可重入锁内执行业务逻辑。
   *
   * @param params 任务参数
   */
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

  /**
   * 实际执行的业务逻辑。
   *
   * @param params 任务参数
   */
  void doRun(Map<String, String> params);
}
