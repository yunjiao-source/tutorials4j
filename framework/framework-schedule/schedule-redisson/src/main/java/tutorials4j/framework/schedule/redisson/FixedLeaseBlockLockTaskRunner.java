package tutorials4j.framework.schedule.redisson;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 使用固定租期阻塞锁执行任务的调度任务接口。
 *
 * <p>任务执行时通过 {@link RedissonBlockLockService} 的固定租期模式加锁，锁在指定的过期时间后自动释放， 适用于可以预估执行耗时的任务。
 *
 * @author Yun Jiao
 */
public interface FixedLeaseBlockLockTaskRunner extends TaskRunner, Lockable {

  /**
   * 在固定租期阻塞锁内执行业务逻辑。
   *
   * @param params 任务参数
   */
  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonBlockLockService.instance
          .fixedLease()
          .doInLock(key(), expireTime(), () -> doRun(params));
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

  /**
   * 固定租期模式下无需设置等待时间。
   *
   * @return 恒为 {@code null}
   */
  @Override
  default Duration waitTime() {
    // 不需要设置
    return null;
  }
}
