package tutorials4j.toolkit.cache.redis.lock;

import cn.hutool.core.util.IdUtil;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.toolkit.cache.redis.script.RedisScriptExecutor;
import tutorials4j.toolkit.core.concurrent.ScheduledThreadPoolExecutorHolder;
import tutorials4j.toolkit.core.exception.CommonErrorCode;
import tutorials4j.toolkit.core.function.ThrowingCallable;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLockService {
  public static final RedisLockService instance = new RedisLockService();

  @Setter private RedisScriptExecutor redisScriptExecutor;
  private volatile FixedLease fixedLease;
  private volatile AutoRenewal autoRenewal;

  public FixedLease fixedLease() {
    if (fixedLease == null) {
      synchronized (this) {
        if (fixedLease == null) {
          fixedLease = new FixedLease(redisScriptExecutor);
        }
      }
    }
    return fixedLease;
  }

  public AutoRenewal autoRenewal() {
    if (autoRenewal == null) {
      synchronized (this) {
        if (autoRenewal == null) {
          autoRenewal = new AutoRenewal(redisScriptExecutor);
        }
      }
    }
    return autoRenewal;
  }

  @RequiredArgsConstructor
  public class FixedLease {
    private final RedisScriptExecutor redisScriptExecutor;

    private String lock(String lockKey, Duration expireTime) {
      String lockId = IdUtil.fastSimpleUUID();

      boolean lockAcquired =
          redisScriptExecutor.setIfAbsent(lockKey, lockId, expireTime.toMillis());

      if (lockAcquired) {
        return lockId;
      }

      return null;
    }

    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      String lockId = null;
      try {
        lockId = lock(lockKey, expireTime);
        if (lockId == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL
              .throwed()
              .param("lockKey", lockKey)
              .param("expireTime", expireTime);
        }
        // 执行业务逻辑
        task.run();
      } finally {
        unlock(lockKey, lockId);
      }
    }

    public <T> T doInLock(String lockKey, Duration expireTime, ThrowingCallable<T> task)
        throws Throwable {
      String lockId = null;
      try {
        lockId = lock(lockKey, expireTime);
        if (lockId == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL
              .throwed()
              .param("lockKey", lockKey)
              .param("expireTime", expireTime);
        }
        // 执行业务逻辑
        return task.call();
      } finally {
        unlock(lockKey, lockId);
      }
    }
  }

  @RequiredArgsConstructor
  public class AutoRenewal {

    /** 锁的默认过期时间。 */
    private static final Duration DEFAULT_EXPIRE_TIME = Duration.ofSeconds(30);

    /** 锁的默认续期周期。 */
    private static final Duration DEFAULT_RENEWAL_PERIOD_TIME = Duration.ofSeconds(9);

    private final RedisScriptExecutor redisScriptExecutor;

    private final Map<String, ScheduledFuture<?>> renewalTasks = new ConcurrentHashMap<>();

    private String lock(String lockKey) {
      String lockId = IdUtil.fastSimpleUUID();

      boolean lockAcquired =
          redisScriptExecutor.setIfAbsent(lockKey, lockId, DEFAULT_EXPIRE_TIME.toMillis());

      if (lockAcquired) {
        renewalLockTask(lockKey, lockId);
        return lockId;
      }

      return null;
    }

    private void renewalLockTask(String lockKey, String lockId) {
      ScheduledFuture<?> future =
          ScheduledThreadPoolExecutorHolder.instance
              .getExecutor()
              .scheduleAtFixedRate(
                  () -> {
                    boolean renewed =
                        redisScriptExecutor.checkAndResetExpire(
                            lockKey, lockId, DEFAULT_EXPIRE_TIME.toMillis());
                    if (!renewed) {
                      // 续期失败（锁已丢失），取消任务
                      cancelLockTask(lockKey);
                    }
                  },
                  DEFAULT_RENEWAL_PERIOD_TIME.toMillis(),
                  DEFAULT_RENEWAL_PERIOD_TIME.toMillis(),
                  TimeUnit.MILLISECONDS);
      renewalTasks.put(lockKey, future);
    }

    private void cancelLockTask(String lockKey) {
      ScheduledFuture<?> task = renewalTasks.remove(lockKey);
      if (task != null) task.cancel(true);
    }

    public void doInLock(String lockKey, Runnable task) {
      String lockId = null;
      try {
        lockId = lock(lockKey);
        if (lockId == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL.throwed().param("lockKey", lockKey);
        }
        // 执行业务逻辑
        task.run();
      } finally {
        cancelLockTask(lockKey);
        unlock(lockKey, lockId);
      }
    }

    public <T> T doInLock(String lockKey, ThrowingCallable<T> task) throws Throwable {
      String lockId = null;
      try {
        lockId = lock(lockKey);
        if (lockId == null) {
          throw CommonErrorCode.ACQUIRE_LOCK_FAIL.throwed().param("lockKey", lockKey);
        }
        // 执行业务逻辑
        return task.call();
      } finally {
        cancelLockTask(lockKey);
        unlock(lockKey, lockId);
      }
    }
  }

  private void unlock(String lockKey, String lockId) {
    if (lockId == null) {
      return;
    }

    boolean unlocked = redisScriptExecutor.deleteIfSame(lockKey, lockId);
    if (!unlocked && log.isDebugEnabled()) {
      log.debug("释放分布式锁不存在，可能因为已过期，lockKey={}", lockKey);
    }
  }
}
