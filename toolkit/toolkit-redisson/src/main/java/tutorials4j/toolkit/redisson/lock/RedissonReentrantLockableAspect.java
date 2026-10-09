package tutorials4j.toolkit.redisson.lock;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
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
public class RedissonReentrantLockableAspect {

  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final RedissonReentrantLockService redissonReentrantLockService;

  @Around("@annotation(redissonReentrantLockable)")
  public Object around(
      ProceedingJoinPoint joinPoint, RedissonReentrantLockable redissonReentrantLockable)
      throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Object[] args = joinPoint.getArgs();

    String value =
        spelMethodBasedExpressionEvaluator.getValue(
            method, args, redissonReentrantLockable.key(), String.class);
    String key = generateKey(redissonReentrantLockable, value);

    TimeUnit timeUnit = redissonReentrantLockable.timeUnit();
    long waitTime = redissonReentrantLockable.waitTime();
    long expireTime = redissonReentrantLockable.expireTime();

    if (expireTime > 0) {
      return redissonReentrantLockService
          .fixedLease()
          .doInLock(
              key,
              Duration.of(waitTime, timeUnit.toChronoUnit()),
              Duration.of(expireTime, timeUnit.toChronoUnit()),
              () -> joinPoint.proceed());
    } else {
      return redissonReentrantLockService
          .autoRenewal()
          .doInLock(key, Duration.of(waitTime, timeUnit.toChronoUnit()), () -> joinPoint.proceed());
    }
  }

  private String generateKey(
      RedissonReentrantLockable redissonReentrantLockable, String argValues) {
    return redissonReentrantLockable.prefix() + argValues;
  }
}
