package tutorials4j.framework.crypto.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD) // 注解仅作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，便于AOP拦截获取
public @interface Crypto {
  boolean response() default false;

  boolean request() default true;
}
