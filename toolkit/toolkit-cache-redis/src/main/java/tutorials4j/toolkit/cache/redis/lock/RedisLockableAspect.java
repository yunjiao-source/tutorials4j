package tutorials4j.toolkit.cache.redis.lock;

import java.lang.reflect.Method;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RedisLockableAspect {

  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final RedisLockService redisLockService;

  @Around("@annotation(redisLockable)")
  public Object around(ProceedingJoinPoint joinPoint, RedisLockable redisLockable)
      throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Object[] args = joinPoint.getArgs();

    String value =
        spelMethodBasedExpressionEvaluator.getValue(
            method, args, redisLockable.key(), String.class);
    String key = generateKey(redisLockable, value);

    long expireTime = redisLockable.expireTime();

    if (expireTime > 0) {
      return redisLockService
          .fixedLease()
          .doInLock(
              key,
              Duration.of(expireTime, redisLockable.timeUnit().toChronoUnit()),
              () -> joinPoint.proceed());
    } else {
      return redisLockService.autoRenewal().doInLock(key, () -> joinPoint.proceed());
    }
  }

  private String generateKey(RedisLockable redisLockable, String argValues) {
    return redisLockable.prefix() + argValues;
  }
}
