package tutorials4j.springboot3.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数校验注解
 *
 * @author Yun Jiao
 */
@Target(ElementType.FIELD) // 作用于字段
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckParam {
  // 是否非空，默认false
  boolean notNull() default false;

  // 字符串最小长度，默认0（不限制）
  int minLength() default 0;

  // 字符串最大长度，默认Integer.MAX_VALUE（不限制）
  int maxLength() default Integer.MAX_VALUE;

  // 校验失败提示信息
  String message() default "";
}
