package tutorials4j.toolkit.redisson.lock;

import java.lang.reflect.Method;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.toolkit.core.constant.DefaultConsts;
import tutorials4j.toolkit.core.context.SpelMethodBasedExpressionEvaluator;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RedissonBlockLockableAspect {
  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final RedissonBlockLockService redissonBlockLockService;

  @Around("@annotation(redissonBlockLockable)")
  public Object around(ProceedingJoinPoint joinPoint, RedissonBlockLockable redissonBlockLockable)
      throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Object[] args = joinPoint.getArgs();

    String value =
        spelMethodBasedExpressionEvaluator.getValue(
            method, args, redissonBlockLockable.key(), String.class);
    String key = generateKey(redissonBlockLockable, value);

    long expireTime = redissonBlockLockable.expireTime();

    if (expireTime > 0) {
      return redissonBlockLockService
          .fixedLease()
          .doInLock(
              key,
              Duration.of(expireTime, redissonBlockLockable.timeUnit().toChronoUnit()),
              () -> joinPoint.proceed());
    } else {
      return redissonBlockLockService.autoRenewal().doInLock(key, () -> joinPoint.proceed());
    }
  }

  private String generateKey(RedissonBlockLockable redissonBlockLockable, String argValues) {
    return DefaultConsts.LOCKABLE_REDIS_KEY_PREFIX + redissonBlockLockable.prefix() + argValues;
  }
}
