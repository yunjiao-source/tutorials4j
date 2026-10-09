package tutorials4j.toolkit.redisson.lock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonReentrantLockService {
  public static final RedissonReentrantLockService instance = new RedissonReentrantLockService();

  public static final int WAIT_SECONDS = 3;

  private static final Duration WAIT_TIME = Duration.ofSeconds(WAIT_SECONDS);

  @Setter private RedissonClient redissonClient;
  private volatile FixedLease fixedLease;
  private volatile AutoRenewal autoRenewal;

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

  private void unlock(RLock lock) {
    if (lock == null) return;
    try {
      if (lock.isHeldByCurrentThread() && lock.isLocked()) {
        lock.unlock();
      }
    } catch (Exception e) {
      throw CommonErrorCode.LOCK_RELEASE_FAIL.throwed(e).param("name", lock.getName());
    }
  }

  @RequiredArgsConstructor
  public class FixedLease {
    private final RedissonClient redissonClient;

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

    public <T> T doInLock(
        String lockKey, Duration waitTime, Duration expireTime, ThrowingCallable<T> task)
        throws Throwable {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime, expireTime);
        if (lock == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL
              .throwed()
              .param("lockKey", lockKey)
              .param("waitTime", waitTime)
              .param("expireTime", expireTime);
        }
        return task.call();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw CommonErrorCode.SYSTEM_EXCEPTION.throwed(e);
      } finally {
        unlock(lock);
      }
    }

    public void doInLock(String lockKey, Duration waitTime, Duration expireTime, Runnable task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime, expireTime);
        if (lock == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL
              .throwed()
              .param("lockKey", lockKey)
              .param("waitTime", waitTime)
              .param("expireTime", expireTime);
        }
        task.run();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw CommonErrorCode.SYSTEM_EXCEPTION.throwed(e);
      } finally {
        unlock(lock);
      }
    }

    public <T> T doInLock(String lockKey, Duration expireTime, ThrowingCallable<T> task)
        throws Throwable {
      return doInLock(lockKey, WAIT_TIME, expireTime, task);
    }

    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      doInLock(lockKey, WAIT_TIME, expireTime, task);
    }
  }

  @RequiredArgsConstructor
  public class AutoRenewal {
    private final RedissonClient redissonClient;

    private RLock tryLock(String lockKey, Duration waitTime) throws InterruptedException {
      RLock lock = redissonClient.getLock(lockKey);
      boolean success = lock.tryLock(waitTime.toMillis(), TimeUnit.MILLISECONDS);
      if (success) {
        return lock;
      }

      return null;
    }

    public <T> T doInLock(String lockKey, Duration waitTime, ThrowingCallable<T> task)
        throws Throwable {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime);
        if (lock == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL
              .throwed()
              .param("lockKey", lockKey)
              .param("waitTime", waitTime);
        }
        return task.call();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw CommonErrorCode.SYSTEM_EXCEPTION.throwed(e);
      } finally {
        unlock(lock);
      }
    }

    public void doInLock(String lockKey, Duration waitTime, Runnable task) {
      RLock lock = null;
      try {
        lock = tryLock(lockKey, waitTime);
        if (lock == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL
              .throwed()
              .param("lockKey", lockKey)
              .param("waitTime", waitTime);
        }
        task.run();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw CommonErrorCode.SYSTEM_EXCEPTION.throwed(e);
      } finally {
        unlock(lock);
      }
    }

    public <T> T doInLock(String lockKey, ThrowingCallable<T> task) throws Throwable {
      return doInLock(lockKey, WAIT_TIME, task);
    }

    public void doInLock(String lockKey, Runnable task) {
      doInLock(lockKey, WAIT_TIME, task);
    }
  }
}
