package tutorials4j.framework.schedule.redisson;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonReentrantLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 使用自动续期可重入锁执行任务的调度任务接口。
 *
 * <p>任务执行时通过 {@link RedissonReentrantLockService} 的自动续期模式加锁，过期时间由看门狗自动续期， 任务执行完毕后自动释放锁。
 *
 * @author Yun Jiao
 */
public interface AutoRenewalReentrantLockTaskRunner extends TaskRunner, Lockable {

  /**
   * 在自动续期可重入锁内执行业务逻辑。
   *
   * @param params 任务参数
   */
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

  /**
   * 实际执行的业务逻辑。
   *
   * @param params 任务参数
   */
  void doRun(Map<String, String> params);

  /**
   * 自动续期模式下无需设置过期时间。
   *
   * @return 恒为 {@code null}
   */
  @Override
  default Duration expireTime() {
    // 不需要设置
    return null;
  }
}
