package tutorials4j.framework.cache.core.lock;

import com.google.common.util.concurrent.Striped;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import tutorials4j.framework.cache.core.exception.CacheErrorCode;
import tutorials4j.framework.cache.core.properties.LocalLockOptions;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.common.core.support.ThrowingCallable;

/**
 * 基于 Guava {@link Striped} 的本地锁服务实现。
 *
 * <p>使用 {@link Striped#lazyWeakLock(int)} 创建固定数量的锁池，根据锁 key 的哈希值分配底层锁实例， 在保证细粒度锁控制的同时有效节省内存。适用于单
 * JVM 内需要按业务 key 互斥执行的场景。
 *
 * <p>提供两类加锁方法：
 *
 * <ul>
 *   <li>带等待时间：使用 {@link Lock#tryLock(long, TimeUnit)}，超时未获取则抛出异常；
 *   <li>不带等待时间：使用 {@link Lock#lock()}，阻塞直到获取锁。
 * </ul>
 *
 * 每种方式都支持无返回值（{@link Runnable}）和有返回值（{@link Supplier}/{@link ThrowingCallable}）两种形式。
 *
 * @author Yun Jiao
 * @see Striped
 * @see LocalLockableAspect
 */
@RequiredArgsConstructor
public class LocalLockService implements InitializingBean {
  private final LocalLockOptions options;
  private volatile Striped<Lock> stripedLock;

  /**
   * 根据锁 key 获取对应的 {@link Lock} 实例。
   *
   * @param lockKey 锁标识 key
   * @return 与该 key 关联的锁实例
   */
  private Lock acquireLock(String lockKey) {
    return stripedLock.get(lockKey);
  }

  /**
   * 在指定等待时间内执行加锁的任务（无返回值）。
   *
   * @param lockKey 锁 key
   * @param waitTime 最大等待时间
   * @param task 需要同步执行的任务
   */
  public void doInLock(String lockKey, Duration waitTime, Runnable task) {
    Lock lock = acquireLock(lockKey);
    try {
      if (lock.tryLock(waitTime.toMillis(), TimeUnit.MILLISECONDS)) {
        try {
          task.run();
        } finally {
          lock.unlock();
        }
      } else {
        throw CacheErrorCode.CACHE_ACCQUIRE_LOCK_FAILURE.throwed().param("lockKey", lockKey);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed(e);
    }
  }

  /**
   * 无超时阻塞式执行加锁的任务（无返回值）。
   *
   * @param lockKey 锁 key
   * @param task 需要同步执行的任务
   */
  public void doInLock(String lockKey, Runnable task) {
    Lock lock = acquireLock(lockKey);

    lock.lock();
    try {
      task.run();
    } finally {
      lock.unlock();
    }
  }

  /**
   * 在指定等待时间内执行加锁的任务（有返回值，允许抛出受检异常）。
   *
   * @param lockKey 锁 key
   * @param waitTime 最大等待时间
   * @param task 需要同步执行并返回结果的任务
   * @param <T> 返回值类型
   * @return 任务的执行结果
   * @throws Throwable 原任务可能抛出的任何异常
   */
  public <T> T doInLock(String lockKey, Duration waitTime, ThrowingCallable<T> task)
      throws Throwable {
    Lock lock = acquireLock(lockKey);
    try {
      if (lock.tryLock(waitTime.toMillis(), TimeUnit.MILLISECONDS)) {
        try {
          return task.call();
        } finally {
          lock.unlock();
        }
      } else {
        throw CacheErrorCode.CACHE_ACCQUIRE_LOCK_FAILURE.throwed().param("lockKey", lockKey);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw BaseErrorCode.WRAP_CHECK_EXCEPTION.throwed(e);
    }
  }

  /**
   * 无超时阻塞式执行加锁的任务（有返回值）。
   *
   * @param lockKey 锁 key
   * @param task 需要同步执行并返回结果的任务
   * @param <T> 返回值类型
   * @return 任务的执行结果
   */
  public <T> T doInLock(String lockKey, ThrowingCallable<T> task) throws Throwable {
    Lock lock = acquireLock(lockKey);
    lock.lock();
    try {
      return task.call();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (stripedLock == null) {
      synchronized (this) {
        if (stripedLock == null) {
          stripedLock = Striped.lazyWeakLock(options.getStripes());
        }
      }
    }

    Assert.notNull(stripedLock, "stripedLock initialization failed");
  }
}
