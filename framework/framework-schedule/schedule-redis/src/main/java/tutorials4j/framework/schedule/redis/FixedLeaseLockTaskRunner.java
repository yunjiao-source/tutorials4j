package tutorials4j.framework.schedule.redis;

import java.time.Duration;
import java.util.Map;
import tutorials4j.framework.cache.core.lock.Lockable;
import tutorials4j.framework.cache.redis.lock.RedisLockService;
import tutorials4j.framework.schedule.core.bean.TaskRunner;

/**
 * 使用 Redis 固定租期锁保护的任务执行器接口。
 *
 * <p>任务执行前通过 {@link RedisLockService} 的固定租期锁模式加锁，按 {@link #expireTime()} 指定的时长持有锁； 加锁成功后调用 {@link
 * #doRun(Map)} 执行实际业务逻辑，异常统一交由 {@link #handleException(Exception)} 处理。
 *
 * @author Yun Jiao
 */
public interface FixedLeaseLockTaskRunner extends TaskRunner, Lockable {

  /**
   * 获取 Redis 固定租期锁并执行任务。
   *
   * @param params 任务参数
   */
  @Override
  default void run(Map<String, String> params) {
    try {
      RedisLockService.instance.fixedLease().doInLock(key(), expireTime(), () -> doRun(params));
    } catch (Exception e) {
      handleException(e);
    }
  }

  /**
   * 在锁保护下执行实际的任务逻辑。
   *
   * @param params 任务参数
   */
  void doRun(Map<String, String> params);

  /**
   * 固定租期模式下无需显式设置锁等待时间。
   *
   * @return 恒为 {@code null}
   */
  @Override
  default Duration waitTime() {
    // 不需要设置
    return null;
  }
}
