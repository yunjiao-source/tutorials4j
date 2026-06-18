package tutorials4j.framework.cache.redis.lock;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import tutorials4j.framework.cache.core.exception.LockCreateException;
import tutorials4j.framework.cache.core.properties.RedisLockOptions;
import tutorials4j.framework.cache.redis.RedisTemplateDecorator;
import tutorials4j.framework.common.core.ExecutionOption;
import tutorials4j.framework.common.core.ExecutorServiceHolder;
import tutorials4j.framework.common.core.support.ThrowingCallable;

/**
 * Redis 分布式锁核心服务类。
 *
 * <p>基于 Redis 的 SET NX PX 命令和 Lua 脚本实现，保证锁操作的原子性。 提供了两种锁模式：
 *
 * <ul>
 *   <li>{@link FixedLease}：固定租期模式，锁在指定时间后自动释放。
 *   <li>{@link AutoRenewal}：自动续期模式，锁持有期间会定期续期，适用于长任务。
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // 固定租期 5 秒
 * redisLockService.fixedLease().doInLock("lockKey", Duration.ofSeconds(5), () -> {
 *     // 业务逻辑
 * });
 *
 * // 自动续期
 * String result = redisLockService.autoRenewal().doInLock("lockKey", () -> {
 *     return "some result";
 * });
 * }</pre>
 *
 * @author Yun Jiao
 * @see FixedLease
 * @see AutoRenewal
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLockService {
  public static final RedisLockService instance = new RedisLockService();

  /** 加锁 Lua 脚本：SET key value NX PX milliseconds */
  private static final RedisScript<String> SCRIPT_LOCK =
      new DefaultRedisScript<>(
          """
          return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])
      """,
          String.class);

  /** 解锁 Lua 脚本：仅当 value 匹配时才删除 key */
  private static final RedisScript<String> SCRIPT_UNLOCK =
      new DefaultRedisScript<>(
          """
          if redis.call('get',KEYS[1]) == ARGV[1] then
            return tostring(redis.call('del', KEYS[1])==1)
          else
            return 'false'
          end
      """,
          String.class);

  /** 续期 Lua 脚本：仅当 value 匹配时才重新设置过期时间 */
  private static final RedisScript<Boolean> SCRIPT_RENEWAL =
      new DefaultRedisScript<>(
          """
          if redis.call('get', KEYS[1]) ==ARGV[1] then
            return redis.call('pexpire', KEYS[1], ARGV[2])
          else
            return 0
          end
      """,
          Boolean.class);

  private static final String LOCK_SUCCESS = "OK";

  @Setter private RedisTemplateDecorator redisTemplateDecorator;
  @Setter private RedisLockOptions redisLockOptions;
  private volatile FixedLease fixedLease;
  private volatile AutoRenewal autoRenewal;

  /**
   * 获取固定租期模式的锁操作器。
   *
   * @return {@link FixedLease} 实例
   */
  public FixedLease fixedLease() {
    if (fixedLease == null) {
      synchronized (this) {
        if (fixedLease == null) {
          fixedLease = new FixedLease(redisTemplateDecorator);
        }
      }
    }
    return fixedLease;
  }

  /**
   * 获取自动续期模式的锁操作器。
   *
   * @return {@link AutoRenewal} 实例
   */
  public AutoRenewal autoRenewal() {
    if (autoRenewal == null) {
      synchronized (this) {
        if (autoRenewal == null) {
          autoRenewal = new AutoRenewal(redisTemplateDecorator, redisLockOptions);
          autoRenewal.initScheduler();
        }
      }
    }
    return autoRenewal;
  }

  /**
   * 固定租期模式的分布式锁实现。
   *
   * <p>锁在获取时指定一个固定的过期时间，到期后自动释放。 不提供自动续期功能，适合执行时间可预估的短任务。
   */
  @RequiredArgsConstructor
  public class FixedLease {

    private final RedisTemplateDecorator redisTemplateDecorator;

    /**
     * 尝试获取锁，内部使用 Lua 脚本保证原子性。
     *
     * @param lockKey 锁的 key
     * @param expireTime 锁的固定租期
     * @return 成功返回唯一锁标识（lockId），失败返回 null
     */
    private String lock(String lockKey, Duration expireTime) {
      String lockId = IdUtil.fastSimpleUUID();

      String value =
          redisTemplateDecorator
              .getStringRedisTemplate()
              .execute(
                  SCRIPT_LOCK,
                  Collections.singletonList(lockKey),
                  lockId,
                  String.valueOf(expireTime.toMillis()));

      if (LOCK_SUCCESS.equals(value)) {
        return lockId;
      }

      return null;
    }

    /**
     * 在分布式锁保护下执行无返回值的任务。
     *
     * @param lockKey 锁的 key
     * @param expireTime 锁的固定租期
     * @param task 要执行的任务
     * @throws LockCreateException 如果获取锁失败（锁已被占用）
     */
    public void doInLock(String lockKey, Duration expireTime, Runnable task) {
      String lockId = null;
      try {
        lockId = lock(lockKey, expireTime);
        if (lockId == null) {
          throw new LockCreateException(lockKey);
        }
        // 执行业务逻辑
        task.run();
      } finally {
        unlock(lockKey, lockId);
      }
    }

    /**
     * 在分布式锁保护下执行有返回值的任务。
     *
     * @param lockKey 锁的 key
     * @param expireTime 锁的固定租期
     * @param task 要执行的任务，可以抛出受检异常
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws Throwable 任务抛出的原始异常
     * @throws LockCreateException 如果获取锁失败
     */
    public <T> T doInLock(String lockKey, Duration expireTime, ThrowingCallable<T> task)
        throws Throwable {
      String lockId = null;
      try {
        lockId = lock(lockKey, expireTime);
        if (lockId == null) {
          throw new LockCreateException(lockKey);
        }
        // 执行业务逻辑
        return task.call();
      } finally {
        unlock(lockKey, lockId);
      }
    }
  }

  /**
   * 自动续期模式的分布式锁实现。
   *
   * <p>锁的默认过期时间为 30 秒，并在每 9 秒自动续期一次。 只要持有锁的业务逻辑仍在运行，锁就会一直被续期，直到业务执行完毕并主动释放。 适用于执行时间不确定的长任务。
   */
  @RequiredArgsConstructor
  public class AutoRenewal {
    private static final Duration DEFAULT_EXPIRE_TIME = Duration.ofSeconds(30);
    private static final Duration DEFAULT_RENEWAL_PERIOD_TIME = Duration.ofSeconds(9);

    private final RedisTemplateDecorator redisTemplateDecorator;
    private final RedisLockOptions redisLockOptions;

    private ExecutorServiceHolder<ScheduledThreadPoolExecutor> executorServiceHolder;
    private final Map<String, ScheduledFuture<?>> renewalTasks = new ConcurrentHashMap<>();
    ;

    /**
     * 尝试获取锁（自动续期模式）。
     *
     * @param lockKey 锁的 key * @return 成功返回唯一锁标识（lockId），失败返回 null
     */
    private String lock(String lockKey) {
      String lockId = IdUtil.fastSimpleUUID();

      String value =
          redisTemplateDecorator
              .getStringRedisTemplate()
              .execute(
                  SCRIPT_LOCK,
                  Collections.singletonList(lockKey),
                  lockId,
                  String.valueOf(DEFAULT_EXPIRE_TIME.toMillis()));

      if (LOCK_SUCCESS.equals(value)) {
        renewalLockTask(lockKey, lockId);
        return lockId;
      }

      return null;
    }

    /**
     * 启动续期定时任务。每次续期后递归调用自身，形成连续续期链。
     *
     * @param lockKey 锁的 key
     * @param lockId 锁的唯一标识
     */
    private void renewalLockTask(String lockKey, String lockId) {
      ScheduledFuture<?> future =
          executorServiceHolder
              .instance()
              .scheduleAtFixedRate(
                  () -> {
                    Boolean renewed =
                        redisTemplateDecorator
                            .getStringRedisTemplate()
                            .execute(
                                SCRIPT_RENEWAL,
                                Collections.singletonList(lockKey),
                                lockId,
                                String.valueOf(DEFAULT_EXPIRE_TIME.toMillis()));
                    if (!Boolean.TRUE.equals(renewed)) {
                      // 续期失败（锁已丢失），取消任务
                      cancelLockTask(lockKey);
                    }
                  },
                  DEFAULT_RENEWAL_PERIOD_TIME.toMillis(),
                  DEFAULT_RENEWAL_PERIOD_TIME.toMillis(),
                  TimeUnit.MILLISECONDS);
      renewalTasks.put(lockKey, future);
    }

    public void initScheduler() {
      ExecutionOption option = redisLockOptions.getAutoRenewal();
      executorServiceHolder = ExecutorServiceHolder.buildScheduler(option);
    }

    /**
     * 在自动续期分布式锁保护下执行无返回值的任务。
     *
     * @param lockKey 锁的 key
     * @param task 要执行的任务
     * @throws LockCreateException 如果获取锁失败（锁已被占用）
     */
    public void doInLock(String lockKey, Runnable task) {
      String lockId = null;
      try {
        lockId = lock(lockKey);
        if (lockId == null) {
          throw new LockCreateException(lockKey);
        }
        // 执行业务逻辑
        task.run();
      } finally {
        cancelLockTask(lockKey);
        unlock(lockKey, lockId);
      }
    }

    /**
     * 在自动续期分布式锁保护下执行有返回值的任务。
     *
     * @param lockKey 锁的 key
     * @param task 要执行的任务，可以抛出受检异常
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws Throwable 任务抛出的原始异常
     * @throws LockCreateException 如果获取锁失败
     */
    public <T> T doInLock(String lockKey, ThrowingCallable<T> task) throws Throwable {
      String lockId = null;
      try {
        lockId = lock(lockKey);
        if (lockId == null) {
          throw new LockCreateException(lockKey);
        }
        // 执行业务逻辑
        return task.call();
      } finally {
        cancelLockTask(lockKey);
        unlock(lockKey, lockId);
      }
    }

    public void destory() {
      if (log.isDebugEnabled()) {
        log.debug("分布式锁自动续期定时任务执行器关闭");
      }
      executorServiceHolder.shutdown();
    }

    private void cancelLockTask(String lockKey) {
      ScheduledFuture<?> task = renewalTasks.remove(lockKey);
      if (task != null) task.cancel(true);
    }
  }

  /**
   * 释放分布式锁（使用 Lua 脚本保证原子性，仅当 lockId 匹配时才删除）。
   *
   * @param lockKey 锁的 key
   * @param lockId 加锁时返回的唯一标识，用于验证持有者身份
   */
  private void unlock(String lockKey, String lockId) {
    if (lockId == null) {
      return;
    }

    String value =
        redisTemplateDecorator
            .getStringRedisTemplate()
            .execute(SCRIPT_UNLOCK, Collections.singletonList(lockKey), lockId);
    if (!Boolean.parseBoolean(value) && log.isDebugEnabled()) {
      log.debug("释放分布式锁不存在，可能因为已过期，lockKey={}", lockKey);
    }
  }

  @PreDestroy
  public void preDestory() {
    if (autoRenewal != null) {
      autoRenewal.destory();
    }
  }
}
