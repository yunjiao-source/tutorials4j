package tutorials4j.framework.cache.redisson.lock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tutorials4j.framework.cache.core.exception.LockException;
import tutorials4j.framework.common.core.support.ThrowingCallable;

/**
 * 基于 Redisson 的阻塞锁服务，提供固定租约和自动续期两种模式。
 *
 * <p>与可重入锁服务的区别在于：本服务使用阻塞式 {@code lock()} 方法， 会一直阻塞直到获取锁，而不是使用 {@code tryLock()}。
 *
 * @author Yun Jiao
 * @see RLock
 * @see RedissonClient
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonBlockLockService {
  private final RedissonClient redissonClient;
  private volatile FixedLease fixedLease;
  private volatile AutoRenewal autoRenewal;

  /**
   * 返回固定租约模式的操作入口。
   *
   * @return 固定租约模式的实例
   */
  public FixedLease fixedLease() {
    if (fixedLease == null) {
      synchronized (this) {
        if (fixedLease == null) {
          fixedLease = new FixedLease(redissonClient);
        }
      }
    }

    return fixedLease;
  }

  /**
   * 返回自动续期模式的操作入口。
   *
   * @return 自动续期模式的实例
   */
  public AutoRenewal autoRenewal() {
    if (autoRenewal == null) {
      synchronized (this) {
        if (autoRenewal == null) {
          autoRenewal = new AutoRenewal(redissonClient);
        }
      }
    }

    return autoRenewal;
  }

  /** 固定租约模式的阻塞锁操作类。 */
  @RequiredArgsConstructor
  public static class FixedLease {
    private final RedissonClient redissonClient;

    /**
     * 阻塞获取锁（固定租约）。
     *
     * @param lockKey 锁的键
     * @param expireTime 锁的持有时间（租约）
     * @return 锁对象
     */
    private RLock lock(String lockKey, Duration expireTime) {
      RLock lock = redissonClient.getLock(lockKey);
      lock.lock(expireTime.toMillis(), TimeUnit.MILLISECONDS);
      return lock;
    }

    /**
     * 在锁保护下执行有返回值的任务。
     *
     * @param lockKey 锁的键
     * @param expireTime 锁的持有时间（租约）
     * @param task 需要执行的任务（Supplier）
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws LockException 解锁异常
     */
    public <T> T doInLock(String lockKey, Duration expireTime, ThrowingCallable<T> task)
        throws Throwable {
      RLock lock = null;
      try {
        lock = lock(lockKey, expireTime);
        return task.call();
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务。
     *
     * @param lockKey 锁的键
     * @param expireTime 锁的持有时间（租约）
     * @param task 需要执行的任务（Runnable）
     * @throws LockException 解锁异常
     */
    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      RLock lock = null;
      try {
        lock = lock(lockKey, expireTime);
        task.run();
      } finally {
        unlock(lock);
      }
    }

    /**
     * 安全释放锁。仅当当前线程持有该锁且锁仍处于锁定状态时才执行解锁。
     *
     * @param lock Redisson 锁对象，可为 null
     * @throws LockException 解锁失败时抛出
     */
    private void unlock(RLock lock) {
      if (lock == null) return;
      try {
        if (lock.isHeldByCurrentThread() && lock.isLocked()) {
          lock.unlock();
        }
      } catch (Exception e) {
        throw new LockException(lock.getName(), e);
      }
    }
  }

  /** 自动续期模式的阻塞锁操作类。 */
  @RequiredArgsConstructor
  public static class AutoRenewal {
    private final RedissonClient redissonClient;

    /**
     * 阻塞获取锁（自动续期）。
     *
     * @param lockKey 锁的键
     * @return 锁对象
     */
    private RLock lock(String lockKey) {
      RLock lock = redissonClient.getLock(lockKey);
      lock.lock();
      return lock;
    }

    /**
     * 在锁保护下执行有返回值的任务。
     *
     * @param lockKey 锁的键
     * @param task 需要执行的任务（Supplier）
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws LockException 解锁异常
     */
    public <T> T doInLock(String lockKey, ThrowingCallable<T> task) throws Throwable {
      RLock lock = null;
      try {
        lock = lock(lockKey);
        return task.call();
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务。
     *
     * @param lockKey 锁的键
     * @param task 需要执行的任务（Runnable）
     * @throws LockException 解锁异常
     */
    public void doInLock(String lockKey, Runnable task) {
      RLock lock = null;
      try {
        lock = lock(lockKey);
        task.run();
      } finally {
        unlock(lock);
      }
    }

    /**
     * 安全释放锁。仅当当前线程持有该锁且锁仍处于锁定状态时才执行解锁。
     *
     * @param lock Redisson 锁对象，可为 null
     * @throws LockException 解锁失败时抛出
     */
    private void unlock(RLock lock) {
      if (lock == null) return;
      try {
        if (lock.isHeldByCurrentThread() && lock.isLocked()) {
          lock.unlock();
        }
      } catch (Exception e) {
        throw new LockException(lock.getName(), e);
      }
    }
  }
}
