package tutorials4j.framework.cache.core.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 用于方法级别的本地锁声明式注解。
 *
 * <p>通过该注解标记的方法会被 {@link LocalLockableAspect} 切面拦截，并基于 SpEL 表达式生成锁的 key， 在方法执行前尝试获取本地锁（基于 {@link
 * com.google.common.util.concurrent.Striped} 的细粒度锁）， 从而保证同一 JVM 内同一锁 key 下的方法执行互斥。
 *
 * <p>示例用法：
 *
 * <pre>
 * &#64;LocalLockable(prefix = "user:", key = "#userId", waitTime = 500)
 * public void updateUser(Long userId, String name) {
 *     // 业务逻辑
 * }
 * </pre>
 *
 * @author Yun Jiao
 * @see LocalLockableAspect
 * @see LocalLockService
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalLockable {
  /**
   * 锁 key 的前缀，用于拼接生成最终锁 key。
   *
   * <p>最终锁 key = prefix + SpEL 表达式求值结果。
   *
   * <p>默认为空字符串，表示无额外前缀。
   *
   * @return 前缀字符串
   */
  String prefix() default "";

  /**
   * 锁 key 的核心部分，支持 SpEL 表达式。
   *
   * <p>表达式会根据被拦截方法的参数进行求值，结果字符串会与 prefix 拼接成最终的锁 key。
   *
   * <p>此属性为必填项。
   *
   * @return SpEL 表达式字符串
   */
  String key();

  /**
   * 尝试获取锁的最大等待时间（数值部分）。
   *
   * @return 等待时间数值，默认 3000 毫秒
   * @see #timeUnit()
   */
  long waitTime() default 3000;

  /**
   * {@link #waitTime()} 的时间单位。
   *
   * <p>默认值为 {@link TimeUnit#MILLISECONDS}。
   *
   * @return 时间单位
   */
  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
