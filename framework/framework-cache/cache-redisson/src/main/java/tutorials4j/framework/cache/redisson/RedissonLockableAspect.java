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
 * TODO
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

  private String generateKey(RedissonLockable redissonLockable, String argValues) {
    return redissonLockable.prefix() + argValues;
  }
}
