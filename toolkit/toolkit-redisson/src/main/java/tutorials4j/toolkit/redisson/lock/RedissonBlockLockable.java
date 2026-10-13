package tutorials4j.toolkit.redisson.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonBlockLockable {
  String prefix() default "";

  String key();

  long expireTime() default -1;

  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
