package tutorials4j.toolkit.redisson.lock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonBlockLockService {
  public static final RedissonBlockLockService instance = new RedissonBlockLockService();

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

  @RequiredArgsConstructor
  public class FixedLease {
    private final RedissonClient redissonClient;

    private RLock lock(String lockKey, Duration expireTime) {
      RLock lock = redissonClient.getLock(lockKey);
      lock.lock(expireTime.toMillis(), TimeUnit.MILLISECONDS);
      return lock;
    }

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

    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      RLock lock = null;
      try {
        lock = lock(lockKey, expireTime);
        task.run();
      } finally {
        unlock(lock);
      }
    }
  }

  @RequiredArgsConstructor
  public class AutoRenewal {
    private final RedissonClient redissonClient;

    private RLock lock(String lockKey) {
      RLock lock = redissonClient.getLock(lockKey);
      lock.lock();
      return lock;
    }

    public <T> T doInLock(String lockKey, ThrowingCallable<T> task) throws Throwable {
      RLock lock = null;
      try {
        lock = lock(lockKey);
        return task.call();
      } finally {
        unlock(lock);
      }
    }

    public void doInLock(String lockKey, Runnable task) {
      RLock lock = null;
      try {
        lock = lock(lockKey);
        task.run();
      } finally {
        unlock(lock);
      }
    }
  }

  private void unlock(RLock lock) {
    if (lock == null) return;
    try {
      if (lock.isHeldByCurrentThread() && lock.isLocked()) {
        lock.unlock();
      }
    } catch (Exception e) {
      // 记录日志，不抛出异常，避免解锁失败会吞掉业务异常
      log.error("释放锁失败", e);
    }
  }
}
