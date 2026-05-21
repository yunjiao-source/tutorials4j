package tutorials4j.framework.cache.redisson.lock;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

/**
 * 切面类，用于处理 {@link RedissonReentrantLockable} 注解。
 *
 * <p>根据注解配置，选择固定租约或自动续期模式，并在方法执行前后加锁/解锁。
 *
 * @author Yun Jiao
 * @see RedissonReentrantLockable
 * @see RedissonReentrantLockService
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

  /**
   * 生成最终的锁 key，由前缀和 SpEL 表达式的求值结果拼接而成。
   *
   * @param redissonReentrantLockable 锁注解配置
   * @param argValues SpEL 表达式求值结果
   * @return 完整的锁 key
   */
  private String generateKey(
      RedissonReentrantLockable redissonReentrantLockable, String argValues) {
    return redissonReentrantLockable.prefix() + argValues;
  }
}
