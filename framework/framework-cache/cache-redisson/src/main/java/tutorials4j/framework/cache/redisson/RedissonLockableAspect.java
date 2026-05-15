package tutorials4j.framework.cache.redisson;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.framework.cache.core.lock.LockCacheType;
import tutorials4j.framework.cache.core.lock.LockServiceFactory;
import tutorials4j.framework.cache.core.lock.LockType;
import tutorials4j.framework.common.core.content.SpelMethodBasedExpressionEvaluator;

/**
 * Redisson 分布式锁的切面实现。
 *
 * <p>拦截带有 {@link RedissonLockable} 注解的方法，根据注解配置动态生成锁的 key， 并通过 {@link LockServiceFactory}
 * 获取对应的锁服务执行锁保护下的方法调用。
 *
 * <p>支持两种锁类型：
 *
 * <ul>
 *   <li>{@link LockType#BLOCK}：阻塞式锁，使用 {@link BlockRedissonLockService}
 *   <li>{@link LockType#REENTRANT}：可重入锁（非阻塞等待），使用 {@link ReentrantRedissonLockService}
 * </ul>
 *
 * @author Yun Jiao
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RedissonLockableAspect {
  private static final Pair<LockCacheType, LockType> REDISSON_BLOCK =
      Pair.of(LockCacheType.REDISSON, LockType.BLOCK);
  private static final Pair<LockCacheType, LockType> REDISSON_REENTRANT =
      Pair.of(LockCacheType.REDISSON, LockType.REENTRANT);

  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final LockServiceFactory lockServiceFactory;

  /**
   * 环绕通知：处理 {@link RedissonLockable} 注解的锁逻辑。
   *
   * @param joinPoint 切点
   * @param redissonLockable 锁注解实例
   * @return 原方法的执行结果
   * @throws Throwable 原方法或锁执行过程中抛出的异常
   */
  @Around("@annotation(redissonLockable)")
  public Object around(ProceedingJoinPoint joinPoint, RedissonLockable redissonLockable)
      throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Object[] args = joinPoint.getArgs();

    String expression =
        spelMethodBasedExpressionEvaluator.getValue(
            method, args, redissonLockable.key(), String.class);
    String key = generateKey(redissonLockable, expression);

    LockType type = redissonLockable.type();
    if (Objects.equals(type, LockType.BLOCK)) {
      BlockRedissonLockService lockService =
          lockServiceFactory.findLockService(REDISSON_BLOCK, BlockRedissonLockService.class);
      return executeWithLock(key, redissonLockable, joinPoint, lockService);
    } else if (Objects.equals(type, LockType.REENTRANT)) {
      ReentrantRedissonLockService lockService =
          lockServiceFactory.findLockService(
              REDISSON_REENTRANT, ReentrantRedissonLockService.class);
      return executeWithLock(key, redissonLockable, joinPoint, lockService);
    }

    throw new IllegalStateException("Unexpected value: " + type);
  }

  /**
   * 执行阻塞式锁（{@link LockType#BLOCK}）的逻辑。
   *
   * @param key 锁的 key
   * @param redissonLockable 锁注解配置
   * @param joinPoint 切点
   * @param lockService 阻塞锁服务
   * @return 方法执行结果
   */
  private Object executeWithLock(
      String key,
      RedissonLockable redissonLockable,
      ProceedingJoinPoint joinPoint,
      BlockRedissonLockService lockService) {
    TimeUnit timeUnit = redissonLockable.timeUnit();
    long expireTime = redissonLockable.expireTime();

    if (expireTime > 0) {
      return lockService
          .fixedLease()
          .doInLock(
              key,
              Duration.of(expireTime, timeUnit.toChronoUnit()),
              () -> {
                try {
                  return joinPoint.proceed();
                } catch (Throwable e) {
                  throw new RuntimeException(e);
                }
              });
    } else {
      return lockService
          .autoRenewal()
          .doInLock(
              key,
              () -> {
                try {
                  return joinPoint.proceed();
                } catch (Throwable e) {
                  throw new RuntimeException(e);
                }
              });
    }
  }

  /**
   * 执行可重入锁（{@link LockType#REENTRANT}）的逻辑。
   *
   * @param key 锁的 key
   * @param redissonLockable 锁注解配置
   * @param joinPoint 切点
   * @param lockService 可重入锁服务
   * @return 方法执行结果
   */
  private Object executeWithLock(
      String key,
      RedissonLockable redissonLockable,
      ProceedingJoinPoint joinPoint,
      ReentrantRedissonLockService lockService) {
    TimeUnit timeUnit = redissonLockable.timeUnit();
    long waitTime = redissonLockable.waitTime();
    long expireTime = redissonLockable.expireTime();

    if (expireTime > 0) {
      return lockService
          .fixedLease()
          .doInLock(
              key,
              Duration.of(waitTime, timeUnit.toChronoUnit()),
              Duration.of(expireTime, timeUnit.toChronoUnit()),
              () -> {
                try {
                  return joinPoint.proceed();
                } catch (Throwable e) {
                  throw new RuntimeException(e);
                }
              });
    } else {
      return lockService
          .autoRenewal()
          .doInLock(
              key,
              Duration.of(waitTime, timeUnit.toChronoUnit()),
              () -> {
                try {
                  return joinPoint.proceed();
                } catch (Throwable e) {
                  throw new RuntimeException(e);
                }
              });
    }
  }

  /**
   * 生成最终的锁 key，由前缀和 SpEL 表达式的求值结果拼接而成。
   *
   * @param redissonLockable 锁注解配置
   * @param argValues SpEL 表达式求值结果
   * @return 完整的锁 key
   */
  private String generateKey(RedissonLockable redissonLockable, String argValues) {
    return redissonLockable.prefix() + argValues;
  }
}
