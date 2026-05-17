package tutorials4j.framework.cache.redisson.lock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tutorials4j.framework.cache.core.exception.LockCreateException;
import tutorials4j.framework.cache.core.exception.LockException;
import tutorials4j.framework.common.core.support.ThrowingCallable;

/**
 * 基于 Redisson 的可重入锁服务，提供固定租约和自动续期两种模式。
 *
 * <p>使用方式：
 *
 * <pre>{@code
 * // 固定租约模式
 * redissonReentrantLockService.fixedLease().doInLock("key", Duration.ofSeconds(1), () -> {...});
 *
 * // 自动续期模式
 * redissonReentrantLockService.autoRenewal().doInLock("key", () -> {...});
 * }</pre>
 *
 * @author Yun Jiao
 * @see RLock
 * @see RedissonClient
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonReentrantLockService {
  /** 默认等待获取锁的时间（秒）。 */
  public static final int WAIT_SECONDS = 3;

  private static final Duration WAIT_TIME = Duration.ofSeconds(WAIT_SECONDS);

  private final RedissonClient redissonClient;

  /**
   * 返回固定租约模式的操作入口。
   *
   * @return 固定租约模式的实例
   */
  public FixedLease fixedLease() {
    return new FixedLease(redissonClient);
  }

  /**
   * 返回自动续期模式的操作入口。
   *
   * @return 自动续期模式的实例
   */
  public AutoRenewal autoRenewal() {
    return new AutoRenewal(redissonClient);
  }

  /**
   * 固定租约模式的可重入锁操作类。
   *
   * <p>该模式下，锁持有时间由租约决定，到期后自动释放，不会续期。
   */
  @RequiredArgsConstructor
  public static class FixedLease {
    private final RedissonClient redissonClient;

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

    /**
     * 尝试获取锁。
     *
     * @param lockKey 锁的键
     * @param waitTime 等待获取锁的最大时间
     * @param expireTime 锁的持有时间（租约）
     * @return 成功获取返回锁对象，否则返回 null
     * @throws InterruptedException 等待过程中被中断
     */
    private RLock tryLock(String lockKey, Duration waitTime, Duration expireTime)
        throws InterruptedException {
      RLock lock = redissonClient.getLock(lockKey);
      boolean success =
          lock.tryLock(waitTime.toMillis(), expireTime.toMillis(), TimeUnit.MILLISECONDS);
      if (success) {
        return lock;
      }

      return null;
    }

    /**
     * 在锁保护下执行有返回值的任务（指定等待时间和租约）。
     *
     * @param lockKey 锁的键
     * @param waitTime 等待获取锁的最大时间
     * @param expireTime 锁的持有时间（租约）
     * @param task 需要执行的任务（Supplier）
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常（包括中断）
     */
    public <T> T doInLock(
        String lockKey, Duration waitTime, Duration expireTime, ThrowingCallable<T> task)
        throws Throwable {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime, expireTime);
        if (lock == null) {
          throw new LockCreateException(lockKey, waitTime);
        }
        return task.call();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new LockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务（指定等待时间和租约）。
     *
     * @param lockKey 锁的键
     * @param waitTime 等待获取锁的最大时间
     * @param expireTime 锁的持有时间（租约）
     * @param task 需要执行的任务（Runnable）
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常（包括中断）
     */
    public void doInLock(String lockKey, Duration waitTime, Duration expireTime, Runnable task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime, expireTime);
        if (lock == null) {
          throw new LockCreateException(lockKey, waitTime);
        }
        task.run();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new LockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行有返回值的任务（使用默认等待时间）。
     *
     * @param lockKey 锁的键
     * @param expireTime 锁的持有时间（租约）
     * @param task 需要执行的任务（Supplier）
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常
     */
    public <T> T doInLock(String lockKey, Duration expireTime, ThrowingCallable<T> task)
        throws Throwable {
      return doInLock(lockKey, WAIT_TIME, expireTime, task);
    }

    /**
     * 在锁保护下执行无返回值的任务（使用默认等待时间）。
     *
     * @param lockKey 锁的键
     * @param expireTime 锁的持有时间（租约）
     * @param task 需要执行的任务（Runnable）
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常
     */
    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      doInLock(lockKey, WAIT_TIME, expireTime, task);
    }
  }

  /**
   * 自动续期模式的可重入锁操作类。
   *
   * <p>该模式下，锁的持有时间会自动续期（通过 Redisson 的看门狗机制）， 直到任务完成手动释放。适用于执行时间不确定的长任务。
   */
  @RequiredArgsConstructor
  public static class AutoRenewal {
    private final RedissonClient redissonClient;

    /**
     * 安全释放锁。
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

    /**
     * 尝试获取锁（自动续期模式）。
     *
     * @param lockKey 锁的键
     * @param waitTime 等待获取锁的最大时间
     * @return 成功获取返回锁对象，否则返回 null
     * @throws InterruptedException 等待过程中被中断
     */
    private RLock tryLock(String lockKey, Duration waitTime) throws InterruptedException {
      RLock lock = redissonClient.getLock(lockKey);
      boolean success = lock.tryLock(waitTime.toMillis(), TimeUnit.MILLISECONDS);
      if (success) {
        return lock;
      }

      return null;
    }

    /**
     * 在锁保护下执行有返回值的任务（指定等待时间）。
     *
     * @param lockKey 锁的键
     * @param waitTime 等待获取锁的最大时间
     * @param task 需要执行的任务（Callable）
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常（包括任务执行异常）
     */
    public <T> T doInLock(String lockKey, Duration waitTime, ThrowingCallable<T> task)
        throws Throwable {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime);
        if (lock == null) {
          throw new LockCreateException(lockKey, waitTime);
        }
        return task.call();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new LockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务（指定等待时间）。
     *
     * @param lockKey 锁的键
     * @param waitTime 等待获取锁的最大时间
     * @param task 需要执行的任务（Runnable）
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常（包括任务执行异常）
     */
    public void doInLock(String lockKey, Duration waitTime, Runnable task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime);
        if (lock == null) {
          throw new LockCreateException(lockKey, waitTime);
        }
        task.run();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new LockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行有返回值的任务（使用默认等待时间）。
     *
     * @param lockKey 锁的键
     * @param task 需要执行的任务（Callable）
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常
     */
    public <T> T doInLock(String lockKey, ThrowingCallable<T> task) throws Throwable {
      return doInLock(lockKey, WAIT_TIME, task);
    }

    /**
     * 在锁保护下执行无返回值的任务（使用默认等待时间）。
     *
     * @param lockKey 锁的键
     * @param task 需要执行的任务（Runnable）
     * @throws LockCreateException 获取锁超时时抛出
     * @throws LockException 其他锁相关异常
     */
    public void doInLock(String lockKey, Runnable task) {
      doInLock(lockKey, WAIT_TIME, task);
    }
  }
}
