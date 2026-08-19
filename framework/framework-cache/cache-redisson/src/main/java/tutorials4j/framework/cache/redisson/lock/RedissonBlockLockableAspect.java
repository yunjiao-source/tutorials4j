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
 * <p>拦截标注了 {@code @RedissonBlockLockable} 的方法：解析 SpEL 表达式得到锁 key，
 * 根据注解配置选择固定租约或自动续期模式，在方法执行前阻塞获取锁、执行后释放锁。
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
   * 环绕通知，在方法执行前阻塞获取 Redisson 锁，执行后释放锁。
   *
   * <p>当 {@code expireTime > 0} 时使用固定租约模式，否则使用自动续期模式。
   *
   * @param joinPoint 切点，代表被拦截的方法
   * @param redissonBlockLockable 方法上标注的 {@link RedissonBlockLockable} 注解实例
   * @return 目标方法的执行结果
   * @throws Throwable 目标方法抛出的异常或锁操作中抛出的异常
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
      return redissonBlockLockService.autoRenewal().doInLock(key, () -> joinPoint.proceed());
    }
  }

  /**
   * 生成最终的锁 key，由前缀 {@link RedissonBlockLockable#prefix()} 和 SpEL 表达式求值结果拼接而成。
   *
   * @param redissonBlockLockable 方法上标注的 {@link RedissonBlockLockable} 注解实例
   * @param argValues SpEL 表达式求值结果
   * @return 完整的锁 key（前缀 + 表达式求值结果）
   */
  private String generateKey(RedissonBlockLockable redissonBlockLockable, String argValues) {
    return redissonBlockLockable.prefix() + argValues;
  }
}
