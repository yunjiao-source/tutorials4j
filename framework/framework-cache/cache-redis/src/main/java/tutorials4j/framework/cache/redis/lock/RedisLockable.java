package tutorials4j.framework.cache.redis.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 标注在方法上的 Redis 分布式锁注解。
 *
 * <p>被注解的方法在执行前会尝试获取一个 Redis 分布式锁，锁的 key 由 {@link #prefix()} 和 SpEL 表达式 {@link #key()}
 * 的计算结果拼接而成。支持两种锁模式：
 *
 * <ul>
 *   <li><b>固定租期模式</b>：通过 {@link #expireTime()} 和 {@link #timeUnit()} 指定锁的持有时间， 超时后锁自动释放，无论业务是否执行完毕。
 *   <li><b>自动续期模式</b>：当 {@link #expireTime()} <= 0 时启用。锁持有期间会自动续期， 确保业务执行完成前锁不会过期（类似看门狗机制）。
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * @RedisLockable(prefix = "user:", key = "#userId", expireTime = 5, timeUnit = TimeUnit.SECONDS)
 * public void updateUser(Long userId, UserDto dto) {
 *     // 业务逻辑
 * }
 * }</pre>
 *
 * @author Yun Jiao
 * @see RedisLockableAspect
 * @see RedisLockService
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLockable {

  /**
   * 锁 key 的固定前缀，直接拼接在 SpEL 表达式结果之前。
   *
   * <p>例如 prefix = "order:"，key = "#orderId"，最终锁 key 为 "order:123"。
   *
   * @return 前缀字符串，默认为空字符串
   */
  String prefix() default "";

  /**
   * 锁 key 的动态部分，支持 Spring SpEL 表达式。
   *
   * <p>表达式可以使用方法参数、参数属性等。例如 "#userId"、"#dto.id"。
   *
   * @return SpEL 表达式，不可为空
   */
  String key();

  /**
   * 锁的固定持有时间（租期），单位由 {@link #timeUnit()} 指定。
   *
   * <p>当值 <= 0 时，自动切换为<b>自动续期模式</b>，锁会被定期续期直到业务执行完成。 当值 > 0
   * 时，使用<b>固定租期模式</b>，锁将在指定时间后自动释放（无论业务是否完成）。
   *
   * @return 过期时间数值，默认 -1 表示自动续期
   */
  long expireTime() default -1;

  /**
   * {@link #expireTime()} 的时间单位。
   *
   * <p>仅在 {@link #expireTime()} > 0 时生效。默认单位为毫秒。
   *
   * @return 时间单位，默认 {@link TimeUnit#MILLISECONDS}
   */
  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
