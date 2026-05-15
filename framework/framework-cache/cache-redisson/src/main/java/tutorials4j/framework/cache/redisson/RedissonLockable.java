package tutorials4j.framework.cache.redisson;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;
import tutorials4j.framework.cache.core.lock.LockType;

/**
 * Redisson 分布式锁注解。
 *
 * <p>标注在方法上，用于声明该方法执行时需要获取分布式锁。 锁的 key 由 {@link #prefix()} 和通过 SpEL 表达式计算的 {@link #key()} 拼接而成。
 *
 * <p>支持两种锁类型：
 *
 * <ul>
 *   <li>{@link LockType#BLOCK}：阻塞式锁，获取不到锁时会一直等待（配合 {@link BlockRedissonLockService}）。
 *   <li>{@link LockType#REENTRANT}：可重入锁，非阻塞等待，可指定等待超时时间（配合 {@link ReentrantRedissonLockService}）。
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>
 * &#64;RedissonLockable(prefix = "user:", key = "#userId", expireTime = 10)
 * public void updateUser(Long userId) { ... }
 * </pre>
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLockable {

  /**
   * 锁 key 的前缀。
   *
   * @return 前缀字符串，默认为空字符串
   */
  String prefix() default "";

  /**
   * 锁 key 的动态部分，支持 SpEL 表达式（基于方法参数）。
   *
   * @return SpEL 表达式，不可为 {@code null}
   */
  String key();

  /**
   * 等待获取锁的超时时间（仅对 {@link LockType#REENTRANT} 类型有效）。
   *
   * @return 等待时间数值，默认为 3
   */
  long waitTime() default 3;

  /**
   * 时间单位，适用于 {@link #waitTime()} 和 {@link #expireTime()}。
   *
   * @return 时间单位，默认为 {@link TimeUnit#SECONDS}
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

  /**
   * 锁的持有时间（租约时间）。若值大于 0 则使用固定租约模式，否则使用自动续期模式（看门狗）。
   *
   * @return 租约时间数值，默认 -1 表示自动续期
   */
  long expireTime() default -1;

  /**
   * 锁类型，默认为阻塞式锁 {@link LockType#BLOCK}。
   *
   * @return 锁类型
   */
  LockType type() default LockType.BLOCK;
}
