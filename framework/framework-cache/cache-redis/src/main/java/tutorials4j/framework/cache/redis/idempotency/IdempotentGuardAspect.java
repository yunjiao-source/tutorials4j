package tutorials4j.framework.cache.redis.idempotency;

import java.lang.reflect.Method;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import tutorials4j.framework.cache.core.exception.CacheErrorCode;
import tutorials4j.framework.cache.redis.script.RedisScriptExecutor;
import tutorials4j.framework.common.spring.content.SpelMethodBasedExpressionEvaluator;

/**
 * 幂等守卫注解 {@link IdempotentGuard} 的 AOP 切面实现。
 *
 * <p>拦截所有标注了 {@code @IdempotentGuard} 的方法，在方法执行前尝试向 Redis 中写入键值对 （键由注解配置的 prefix 和 key 的 SPEL
 * 计算值拼接而成，值为当前时间戳）。 若写入成功（键不存在），则放行方法执行；若写入失败（键已存在），则认为存在并发重复请求， 抛出幂等失败异常 {@code
 * CacheErrorCode.IDEMPOTENT_FAIL}。
 *
 * <p>通过 Redis 的 {@code setIfAbsent} 原子操作保证分布式环境下的幂等性。
 *
 * @author Yun Jiao
 * @see IdempotentGuard
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class IdempotentGuardAspect {

  private final SpelMethodBasedExpressionEvaluator spelMethodBasedExpressionEvaluator;
  private final RedisScriptExecutor redisScriptExecutor;

  /**
   * 环绕增强方法，处理幂等守卫逻辑。
   *
   * @param joinPoint 切点连接点
   * @param idempotentGuard 方法上的幂等守卫注解实例
   * @return 目标方法执行结果
   * @throws Throwable 目标方法或幂等校验可能抛出的异常
   */
  @Around("@annotation(idempotentGuard)")
  public Object around(ProceedingJoinPoint joinPoint, IdempotentGuard idempotentGuard)
      throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Object[] args = joinPoint.getArgs();

    // 通过 SPEL 计算动态 key 值
    String value =
        spelMethodBasedExpressionEvaluator.getValue(
            method, args, idempotentGuard.key(), String.class);
    String key = generateKey(idempotentGuard, value);
    String nowStr = Instant.now().toString();

    // 尝试设置键，若键已存在则返回 false
    boolean success = redisScriptExecutor.setIfAbsent(key, nowStr, idempotentGuard.expireMills());

    if (!success) {
      // 重复请求，抛出幂等异常
      throw CacheErrorCode.IDEMPOTENT_FAIL.throwed().param("key", key);
    }

    // 首次执行，放行
    try {
      return joinPoint.proceed();
    } catch (Throwable t) {
      // 根据注解配置决定是否清除键
      if (idempotentGuard.clearOnFailure()) {
        try {
          redisScriptExecutor.deleteIfSame(key, nowStr);
        } catch (Exception e) {
          log.error("清除幂等键失败 key={}", key, e);
        }
      }
      throw t;
    }
  }

  /**
   * 生成 Redis 存储键。
   *
   * <p>由注解中的 {@code prefix} 与计算出的 SPEL 值拼接而成。
   *
   * @param idempotentGuard 注解实例
   * @param argValues SPEL 表达式计算后的键值
   * @return 完整的 Redis 键
   */
  private String generateKey(IdempotentGuard idempotentGuard, String argValues) {
    return idempotentGuard.prefix() + argValues;
  }
}
