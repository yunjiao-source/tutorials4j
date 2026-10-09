package tutorials4j.toolkit.redisson.lock;

import static tutorials4j.toolkit.core.constant.ToolkitConsts.LOCKABLE_REDIS_KEY_PREFIX;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonBlockLockable {
  String prefix() default LOCKABLE_REDIS_KEY_PREFIX;

  String key();

  long expireTime() default -1;

  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
