package tutorials4j.framework.cache.redisson.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 标记一个方法需要被 Redisson 阻塞锁保护，支持 SpEL 表达式作为锁 key。
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * @RedissonBlockLockable(prefix = "order:", key = "#orderId", expireTime = 3000)
 * public void processOrder(Long orderId) { ... }
 * }</pre>
 *
 * <p>阻塞锁会一直等待直到获取锁，不会超时。当 {@code expireTime > 0} 时使用固定租约模式， 否则使用自动续期模式。
 *
 * @author Yun Jiao
 * @see RedissonBlockLockService
 * @see RedissonBlockLockableAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonBlockLockable {
  /**
   * 锁 key 的前缀。
   *
   * @return 前缀字符串，默认为空
   */
  String prefix() default "";

  /**
   * 锁 key 的 SpEL 表达式（必填）。
   *
   * <p>支持方法参数和 Spring 表达式，例如 {@code "#id"}、{@code "#user.name"}。
   *
   * @return SpEL 表达式
   */
  String key();

  /**
   * 锁的持有时间（租约）。当值 ≤ 0 时表示使用自动续期模式。
   *
   * @return 租约时长，默认 -1（自动续期）
   */
  long expireTime() default -1;

  /**
   * 租约时间的时间单位。
   *
   * @return 时间单位，默认毫秒
   */
  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
