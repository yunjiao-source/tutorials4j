package tutorials4j.framework.cache.redis.lock;

import java.lang.reflect.Method;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.framework.common.core.content.SpelMethodBasedExpressionEvaluator;

/**
 * {@link RedisLockable} 注解的 AOP 切面实现。
 *
 * <p>拦截所有标注了 {@code @RedisLockable} 的方法，根据注解配置获取分布式锁， 并在锁保护下执行目标方法。支持两种锁模式：
 *
 * <ul>
 *   <li>固定租期模式：使用 {@link RedisLockService.FixedLease}
 *   <li>自动续期模式：使用 {@link RedisLockService.AutoRenewal}
 * </ul>
 *
 * <p>锁的完整 key = {@link RedisLockable#prefix()} + SpEL 表达式求值结果。
 *
 * @author Yun Jiao
 * @see RedisLockable
 * @see RedisLockService
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RedisLockableAspect {

  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final RedisLockService redisLockService;

  /**
   * 环绕通知：获取分布式锁并执行目标方法。
   *
   * <p>流程：
   *
   * <ol>
   *   <li>解析注解中的 SpEL 表达式 {@link RedisLockable#key()}，得到动态 key 值。
   *   <li>拼接前缀得到完整锁 key。
   *   <li>根据 {@code expireTime} 选择固定租期或自动续期模式，调用对应的 {@code doInLock} 方法。
   *   <li>在锁保护下执行 {@code joinPoint.proceed()}。
   * </ol>
   *
   * @param joinPoint 切点，代表被拦截的方法
   * @param redisLockable 方法上的 {@code RedisLockable} 注解实例
   * @return 目标方法的执行结果
   * @throws Throwable 目标方法或锁操作中抛出的异常
   */
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

  /**
   * 生成最终的锁 key，由前缀和 SpEL 表达式的求值结果拼接而成。
   *
   * @param redissonBlockLockable 锁注解配置
   * @param argValues SpEL 表达式求值结果
   * @return 完整的锁 key
   */
  private String generateKey(RedisLockable redissonBlockLockable, String argValues) {
    return redissonBlockLockable.prefix() + argValues;
  }
}
