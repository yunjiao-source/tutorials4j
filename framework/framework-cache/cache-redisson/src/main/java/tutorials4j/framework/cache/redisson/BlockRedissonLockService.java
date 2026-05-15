package tutorials4j.framework.cache.redisson;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tutorials4j.framework.cache.core.exception.DistributedLockException;
import tutorials4j.framework.cache.core.lock.LockCacheType;
import tutorials4j.framework.cache.core.lock.LockService;
import tutorials4j.framework.cache.core.lock.LockType;

/**
 * 基于 Redisson 的分布式锁执行器，支持两种模式：固定租约时间模式和自动续期模式。
 *
 * <p>两种模式均为阻塞式获取锁（即获取不到锁时会一直等待直到获得锁）。
 *
 * <ul>
 *   <li><b>固定租约时间模式 ({@link FixedLease})</b>：使用 {@link RLock#lock(long, TimeUnit)} 方法，
 *       锁持有时间固定，到期自动释放，不会自动续期。
 *   <li><b>自动续期模式 ({@link AutoRenewal})</b>：使用 {@link RLock#lock()} 方法， Redisson 会启动“看门狗”自动续期（默认每
 *       10 秒续期一次，直到手动释放锁）。
 * </ul>
 *
 * @author Yun Jiao
 * @see ReentrantRedissonLockService 支持非阻塞尝试获取锁的版本
 */
@Slf4j
@RequiredArgsConstructor
public class BlockRedissonLockService implements LockService {
  private final RedissonClient redissonClient;

  /**
   * 获取固定租约时间模式的锁执行器实例。
   *
   * @return 固定租约时间模式的执行器
   */
  public FixedLease fixedLease() {
    return new FixedLease(redissonClient);
  }

  /**
   * 获取自动续期模式的锁执行器实例。
   *
   * @return 自动续期模式的执行器
   */
  public AutoRenewal autoRenewal() {
    return new AutoRenewal(redissonClient);
  }

  @Override
  public LockCacheType getLockCacheType() {
    return LockCacheType.REDISSON;
  }

  @Override
  public LockType getLockType() {
    return LockType.BLOCK;
  }

  /**
   * 固定租约时间模式的分布式锁执行器（阻塞式获取锁，锁持有固定时间，无自动续期）。
   *
   * <p>使用 {@link RLock#lock(long, TimeUnit)} 方法，锁在指定的 {@code expireTime} 后自动释放。
   * 若业务执行时间超过租约时间，锁会被强制释放，可能导致并发问题，因此请合理评估租约时长。
   */
  @RequiredArgsConstructor
  public static class FixedLease {
    private final RedissonClient redissonClient;

    /**
     * 安全释放锁。仅当当前线程持有该锁且锁仍处于锁定状态时才执行解锁。
     *
     * @param lock Redisson 锁对象，可为 null
     * @throws DistributedLockException 解锁失败时抛出
     */
    private void unlock(RLock lock) {
      if (lock == null) return;
      try {
        if (lock.isHeldByCurrentThread() && lock.isLocked()) {
          lock.unlock();
        }
      } catch (Exception e) {
        throw new DistributedLockException(lock.getName(), e);
      }
    }

    /**
     * 阻塞式获取锁，并指定锁的持有时间（固定租约）。
     *
     * @param lockKey 锁的键名
     * @param expireTime 锁的持有时间，到期自动释放
     * @return 获取到的锁对象
     */
    private RLock lock(String lockKey, Duration expireTime) {
      RLock lock = redissonClient.getLock(lockKey);
      lock.lock(expireTime.toMillis(), TimeUnit.MILLISECONDS);
      return lock;
    }

    /**
     * 在锁保护下执行有返回值的任务（固定租约模式）。
     *
     * <p>阻塞等待直到获取锁，持有锁的时间上限为 {@code expireTime}。 任务执行完成后自动释放锁，若执行过程中抛出异常也会释放锁。
     *
     * @param lockKey 锁的键名
     * @param expireTime 锁的持有时间，到期自动释放（不续期）
     * @param task 需要执行的任务，返回结果
     * @param <T> 任务返回值类型
     * @return 任务执行结果
     * @throws DistributedLockException 获取锁失败或任务执行异常时抛出（包含锁键名和原因）
     */
    public <T> T doInLock(String lockKey, Duration expireTime, Callable<T> task) {
      RLock lock = null;
      try {
        lock = lock(lockKey, expireTime);
        return task.call();
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务（固定租约模式）。
     *
     * <p>阻塞等待直到获取锁，持有锁的时间上限为 {@code expireTime}。 任务执行完成后自动释放锁，若执行过程中抛出异常也会释放锁。
     *
     * @param lockKey 锁的键名
     * @param expireTime 锁的持有时间，到期自动释放（不续期）
     * @param task 需要执行的任务（无返回值）
     * @throws DistributedLockException 获取锁失败或任务执行异常时抛出（包含锁键名和原因）
     */
    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      RLock lock = null;
      try {
        lock = lock(lockKey, expireTime);
        task.run();
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }
  }

  /**
   * 自动续期模式的分布式锁执行器（阻塞式获取锁，看门狗自动续期）。
   *
   * <p>使用 {@link RLock#lock()} 方法，无参调用时 Redisson 会自动启动看门狗线程， 默认每 10 秒将锁的过期时间延长一次，确保业务执行期间锁不会自动释放。
   *
   * <p><b>注意：</b> 该模式下必须手动调用 {@code unlock} 释放锁（由框架自动完成）， 因为看门狗不会主动释放锁，若一直不释放则锁会永远存在。
   */
  @RequiredArgsConstructor
  public static class AutoRenewal {
    private final RedissonClient redissonClient;

    /**
     * 安全释放锁。仅当当前线程持有该锁且锁仍处于锁定状态时才执行解锁。
     *
     * @param lock Redisson 锁对象，可为 null
     * @throws DistributedLockException 解锁失败时抛出
     */
    private void unlock(RLock lock) {
      if (lock == null) return;
      try {
        if (lock.isHeldByCurrentThread() && lock.isLocked()) {
          lock.unlock();
        }
      } catch (Exception e) {
        throw new DistributedLockException(lock.getName(), e);
      }
    }

    /**
     * 阻塞式获取锁，自动续期（看门狗模式）。
     *
     * @param lockKey 锁的键名
     * @return 获取到的锁对象
     */
    private RLock lock(String lockKey) {
      RLock lock = redissonClient.getLock(lockKey);
      lock.lock();
      return lock;
    }

    /**
     * 在锁保护下执行有返回值的任务（自动续期模式）。
     *
     * <p>阻塞等待直到获取锁，看门狗自动续期直到锁被手动释放。 任务执行完成后自动释放锁，若执行过程中抛出异常也会释放锁。
     *
     * @param lockKey 锁的键名
     * @param task 需要执行的任务，返回结果
     * @param <T> 任务返回值类型
     * @return 任务执行结果
     * @throws DistributedLockException 获取锁失败或任务执行异常时抛出（包含锁键名和原因）
     */
    public <T> T doInLock(String lockKey, Callable<T> task) {
      RLock lock = null;
      try {
        lock = lock(lockKey);
        return task.call();
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务（自动续期模式）。
     *
     * <p>阻塞等待直到获取锁，看门狗自动续期直到锁被手动释放。 任务执行完成后自动释放锁，若执行过程中抛出异常也会释放锁。
     *
     * @param lockKey 锁的键名
     * @param task 需要执行的任务（无返回值）
     * @throws DistributedLockException 获取锁失败或任务执行异常时抛出（包含锁键名和原因）
     */
    public void doInLock(String lockKey, Runnable task) {
      RLock lock = null;
      try {
        lock = lock(lockKey);
        task.run();
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }
  }
}
