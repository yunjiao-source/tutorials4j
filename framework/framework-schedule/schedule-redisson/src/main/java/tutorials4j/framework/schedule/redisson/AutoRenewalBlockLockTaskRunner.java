package tutorials4j.framework.schedule.redisson;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redisson.lock.RedissonBlockLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 使用自动续期阻塞锁执行任务的调度任务接口。
 *
 * <p>任务执行时通过 {@link RedissonBlockLockService} 的自动续期模式加锁，锁过期时间无需手动设置， 由看门狗机制自动续期，任务执行完毕后自动释放锁。
 *
 * @author Yun Jiao
 */
public interface AutoRenewalBlockLockTaskRunner extends TaskRunner, Lockable {

  /**
   * 在自动续期阻塞锁内执行业务逻辑。
   *
   * @param params 任务参数
   */
  @Override
  default void run(Map<String, String> params) {
    try {
      RedissonBlockLockService.instance.autoRenewal().doInLock(key(), () -> doRun(params));
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
   * 自动续期模式下无需设置等待时间。
   *
   * @return 恒为 {@code null}
   */
  @Override
  default Duration waitTime() {
    // 不需要设置
    return null;
  }

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
