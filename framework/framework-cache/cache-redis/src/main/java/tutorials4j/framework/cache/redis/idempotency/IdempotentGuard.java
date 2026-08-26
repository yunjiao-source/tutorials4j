package tutorials4j.framework.cache.redis.idempotency;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等性守卫注解。
 *
 * <p>标注在方法上，用于防止重复执行（幂等控制）。基于 Redis 实现分布式幂等锁， 通过 SPEL 表达式动态生成唯一键，在方法执行前尝试设置缓存键，若键已存在则抛出幂等异常。
 *
 * <p><b>使用示例：</b>
 *
 * <pre>
 * &#064;IdempotentGuard(
 *     prefix = "order:pay:",
 *     key = "#orderId + ':' + #userId",
 *     expireTime = 5,
 *     timeUnit = TimeUnit.MINUTES
 * )
 * public void payOrder(String orderId, String userId) {
 *     // 业务逻辑
 * }
 * </pre>
 *
 * @author Yun Jiao
 * @see IdempotentGuardAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdempotentGuard {

  /**
   * Redis 键的前缀，默认为空字符串。
   *
   * <p>可结合 {@link #key()} 共同构成最终存储键。
   *
   * @return 前缀字符串
   */
  String prefix() default "";

  /**
   * 动态键的 SPEL 表达式，必须指定。
   *
   * <p>方法执行时会根据参数、上下文等计算实际值，作为幂等标识的一部分。
   *
   * @return SPEL 表达式
   */
  String key();

  /**
   * 幂等记录的过期时间(毫秒)，默认：1分钟
   *
   * @return 过期数值
   */
  long expireMills() default 600000;

  /**
   * 失败时是否清除键
   *
   * @return 返回true，清除
   */
  boolean clearOnFailure() default true;
}
