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
 * 基于 Redisson 的可重入分布式锁执行器，支持非阻塞尝试获取锁（带等待超时）。
 *
 * <p>与 {@link BlockRedissonLockService} 的区别在于：
 *
 * <ul>
 *   <li>使用 {@link RLock#tryLock(long, long, TimeUnit)} 或 {@link RLock#tryLock(long, TimeUnit)}，
 *       允许指定等待获取锁的超时时间。
 *   <li>若在等待时间内未能获取锁，会立即抛出 {@link DistributedLockException}，而不是无限阻塞。
 *   <li>同样提供固定租约模式（需指定租约时间）和自动续期模式（看门狗）。
 * </ul>
 *
 * <p>默认等待时间为 3 秒，可通过 {@link #WAIT_SECONDS} 修改。
 *
 * @author Yun Jiao
 * @see BlockRedissonLockService 阻塞式版本（无限等待直到获取锁）
 */
@Slf4j
@RequiredArgsConstructor
public class ReentrantRedissonLockService implements LockService {
  /** 默认等待获取锁的时间（秒）。 */
  public static final int WAIT_SECONDS = 3;

  private static final Duration WAIT_TIME = Duration.ofSeconds(WAIT_SECONDS);

  private final RedissonClient redissonClient;

  /**
   * 获取固定租约时间模式的锁执行器实例（可重入，非阻塞等待）。
   *
   * @return 固定租约时间模式的执行器
   */
  public FixedLease fixedLease() {
    return new FixedLease(redissonClient);
  }

  /**
   * 获取自动续期模式的锁执行器实例（可重入，非阻塞等待）。
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
    return LockType.REENTRANT;
  }

  /**
   * 固定租约时间模式的分布式锁执行器（可重入，非阻塞等待，锁持有固定时间，无自动续期）。
   *
   * <p>使用 {@link RLock#tryLock(long, long, TimeUnit)} 方法。 需同时指定等待获取锁的超时时间和锁的租约时间。
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
     * 尝试获取锁，非阻塞等待，指定等待时间和租约时间。
     *
     * @param lockKey 锁的键名
     * @param waitTime 等待获取锁的最大时间
     * @param expireTime 锁的持有时间（租约时间），到期自动释放
     * @return 获取成功返回锁对象，超时未获取到则返回 {@code null}
     * @throws InterruptedException 等待过程中线程被中断时抛出
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
     * 在锁保护下执行有返回值的任务（固定租约模式，自定义等待时间和租约时间）。
     *
     * <p>在指定的 {@code waitTime} 内尝试获取锁，若成功则执行任务，锁的持有时间上限为 {@code expireTime}。 超时未获取到锁会抛出异常。
     *
     * @param lockKey 锁的键名
     * @param waitTime 等待获取锁的最大时间
     * @param expireTime 锁的持有时间，到期自动释放（不续期）
     * @param task 需要执行的任务，返回结果
     * @param <T> 任务返回值类型
     * @return 任务执行结果
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     */
    public <T> T doInLock(
        String lockKey, Duration waitTime, Duration expireTime, Callable<T> task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime, expireTime);
        if (lock == null) {
          throw new DistributedLockException(lockKey, waitTime);
        }
        return task.call();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new DistributedLockException(lockKey, e);
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务（固定租约模式，自定义等待时间和租约时间）。
     *
     * @param lockKey 锁的键名
     * @param waitTime 等待获取锁的最大时间
     * @param expireTime 锁的持有时间，到期自动释放（不续期）
     * @param task 需要执行的任务（无返回值）
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     * @see #doInLock(String, Duration, Duration, Callable)
     */
    public void doInLock(String lockKey, Duration waitTime, Duration expireTime, Runnable task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime, expireTime);
        if (lock == null) {
          throw new DistributedLockException(lockKey, waitTime);
        }
        task.run();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new DistributedLockException(lockKey, e);
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行有返回值的任务（固定租约模式，使用默认等待时间 {@value ReentrantRedissonLockService#WAIT_SECONDS} 秒）。
     *
     * @param lockKey 锁的键名
     * @param expireTime 锁的持有时间，到期自动释放（不续期）
     * @param task 需要执行的任务，返回结果
     * @param <T> 任务返回值类型
     * @return 任务执行结果
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     */
    public <T> T doInLock(String lockKey, Duration expireTime, Callable<T> task) {
      return doInLock(lockKey, WAIT_TIME, expireTime, task);
    }

    /**
     * 在锁保护下执行无返回值的任务（固定租约模式，使用默认等待时间 {@value ReentrantRedissonLockService#WAIT_SECONDS} 秒）。
     *
     * @param lockKey 锁的键名
     * @param expireTime 锁的持有时间，到期自动释放（不续期）
     * @param task 需要执行的任务（无返回值）
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     */
    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      doInLock(lockKey, WAIT_TIME, expireTime, task);
    }
  }

  /**
   * 自动续期模式的分布式锁执行器（可重入，非阻塞等待，看门狗自动续期）。
   *
   * <p>使用 {@link RLock#tryLock(long, TimeUnit)} 方法。 只指定等待获取锁的超时时间，不指定租约时间，Redisson 会自动启用看门狗进行续期。
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
     * 尝试获取锁并自动续期（看门狗），非阻塞等待。
     *
     * @param lockKey 锁的键名
     * @param waitTime 等待获取锁的最大时间
     * @return 获取成功返回锁对象，超时未获取到则返回 {@code null}
     * @throws InterruptedException 等待过程中线程被中断时抛出
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
     * 在锁保护下执行有返回值的任务（自动续期模式，自定义等待时间）。
     *
     * <p>在指定的 {@code waitTime} 内尝试获取锁，成功则执行任务，锁由看门狗自动续期。 超时未获取到锁会抛出异常。
     *
     * @param lockKey 锁的键名
     * @param waitTime 等待获取锁的最大时间
     * @param task 需要执行的任务，返回结果
     * @param <T> 任务返回值类型
     * @return 任务执行结果
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     */
    public <T> T doInLock(String lockKey, Duration waitTime, Callable<T> task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime);
        if (lock == null) {
          throw new DistributedLockException(lockKey, waitTime);
        }
        return task.call();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new DistributedLockException(lockKey, e);
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行无返回值的任务（自动续期模式，自定义等待时间）。
     *
     * @param lockKey 锁的键名
     * @param waitTime 等待获取锁的最大时间
     * @param task 需要执行的任务（无返回值）
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     * @see #doInLock(String, Duration, Callable)
     */
    public void doInLock(String lockKey, Duration waitTime, Runnable task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime);
        if (lock == null) {
          throw new DistributedLockException(lockKey, waitTime);
        }
        task.run();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new DistributedLockException(lockKey, e);
      } catch (Exception e) {
        throw new DistributedLockException(lockKey, e);
      } finally {
        unlock(lock);
      }
    }

    /**
     * 在锁保护下执行有返回值的任务（自动续期模式，使用默认等待时间 {@value ReentrantRedissonLockService#WAIT_SECONDS} 秒）。
     *
     * @param lockKey 锁的键名
     * @param task 需要执行的任务，返回结果
     * @param <T> 任务返回值类型
     * @return 任务执行结果
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     */
    public <T> T doInLock(String lockKey, Callable<T> task) {
      return doInLock(lockKey, WAIT_TIME, task);
    }

    /**
     * 在锁保护下执行无返回值的任务（自动续期模式，使用默认等待时间 {@value ReentrantRedissonLockService#WAIT_SECONDS} 秒）。
     *
     * @param lockKey 锁的键名
     * @param task 需要执行的任务（无返回值）
     * @throws DistributedLockException 获取锁超时、任务执行异常或解锁失败时抛出
     */
    public void doInLock(String lockKey, Runnable task) {
      doInLock(lockKey, WAIT_TIME, task);
    }
  }
}
