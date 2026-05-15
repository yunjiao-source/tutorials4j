package tutorials4j.framework.cache.redisson;

import tutorials4j.framework.cache.core.lock.LockType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLockable {
    String prefix() default "";

    String key();

    long waitTime() default 3;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long expireTime() default -1;

    LockType type() default LockType.BLOCK;
}
