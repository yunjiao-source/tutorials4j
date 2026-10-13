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
public @interface RedissonReentrantLockable {
  String prefix() default "";

  String key();

  long waitTime() default 3000;

  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

  long expireTime() default -1;
}
