package tutorials4j.framework.cache.redisson.lock;

import java.lang.reflect.Method;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

/**
 * 切面类，用于处理 {@link RedissonBlockLockable} 注解。
 *
 * <p>根据注解配置，选择固定租约或自动续期模式，并在方法执行前后加锁/解锁。
 *
 * @author Yun Jiao
 * @see RedissonBlockLockable
 * @see RedissonBlockLockService
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RedissonBlockLockableAspect {

  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final RedissonBlockLockService redissonBlockLockService;

  /**
   * 环绕通知，在方法执行前获取锁（阻塞），执行后释放锁。
   *
   * @param joinPoint 切点
   * @param redissonBlockLockable 锁注解
   * @return 原方法的执行结果
   * @throws Throwable 原方法抛出的异常或锁异常
   */
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
      return redissonBlockLockService
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
   * 生成最终的锁 key，由前缀和 SpEL 表达式的求值结果拼接而成。
   *
   * @param redissonBlockLockable 锁注解配置
   * @param argValues SpEL 表达式求值结果
   * @return 完整的锁 key
   */
  private String generateKey(RedissonBlockLockable redissonBlockLockable, String argValues) {
    return redissonBlockLockable.prefix() + argValues;
  }
}
