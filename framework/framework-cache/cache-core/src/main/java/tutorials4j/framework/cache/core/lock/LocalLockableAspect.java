package tutorials4j.framework.cache.core.lock;

import java.lang.reflect.Method;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

/**
 * {@link LocalLockable} 注解的 AOP 切面实现。
 *
 * <p>拦截所有标注了 {@code @LocalLockable} 的方法，解析 SpEL 表达式生成锁的 key， 并委托给 {@link LocalLockService}
 * 执行带锁的方法调用。
 *
 * <p>等待时间的处理逻辑：
 *
 * <ul>
 *   <li>如果 {@link LocalLockable#waitTime()} > 0，则使用带超时的 tryLock 方式；
 *   <li>否则使用无超时的 lock 方式（阻塞直到获取锁）。
 * </ul>
 *
 * @author Yun Jiao
 * @see LocalLockable
 * @see LocalLockService
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class LocalLockableAspect {

  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final LocalLockService localLockService;

  /**
   * 环绕通知：为被 {@link LocalLockable} 标记的方法添加本地锁控制。
   *
   * @param joinPoint 切点连接点
   * @param localLockable 方法上的锁注解
   * @return 原方法的执行结果
   */
  @Around("@annotation(localLockable)")
  public Object around(ProceedingJoinPoint joinPoint, LocalLockable localLockable)
      throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Object[] args = joinPoint.getArgs();

    String value =
        spelMethodBasedExpressionEvaluator.getValue(
            method, args, localLockable.key(), String.class);
    String key = generateKey(localLockable, value);

    long waitTime = localLockable.waitTime();

    if (waitTime >= 0) {
      return localLockService.doInLock(
          key,
          Duration.of(waitTime, localLockable.timeUnit().toChronoUnit()),
          () -> joinPoint.proceed());
    } else {
      return localLockService.doInLock(key, () -> joinPoint.proceed());
    }
  }

  /**
   * 生成最终的锁 key，由前缀和 SpEL 表达式的求值结果拼接而成。
   *
   * @param redissonBlockLockable 锁注解配置
   * @param argValues SpEL 表达式求值结果
   * @return 完整的锁 key
   */
  private String generateKey(LocalLockable redissonBlockLockable, String argValues) {
    String key = redissonBlockLockable.prefix() + argValues;
    if (StringUtils.isBlank(key)) {
      throw new IllegalStateException("锁键是空的");
    }
    return key;
  }
}
