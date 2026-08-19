package tutorials4j.framework.cache.core.lock;

import java.time.Duration;

/**
 * 可加锁任务接口。
 *
 * <p>为调度任务提供锁相关的 key、等待时间与过期时间配置，以及异常处理钩子， 由各类加锁任务执行器配合使用。
 *
 * @author Yun Jiao
 */
public interface Lockable {
  /**
   * 返回锁的 key。
   *
   * @return 锁 key
   */
  String key();

  /**
   * 返回尝试获取锁的最大等待时间。
   *
   * @return 等待时间，为 {@code null} 时表示无需等待限制
   */
  Duration waitTime();

  /**
   * 返回锁的过期时间。
   *
   * @return 过期时间，为 {@code null} 时表示无需过期限制
   */
  Duration expireTime();

  /**
   * 处理任务执行过程中捕获的异常，默认为空实现。
   *
   * @param exception 捕获的异常
   */
  default void handleException(Exception exception) {}
}
