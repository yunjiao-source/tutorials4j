package tutorials4j.springboot3;

import java.lang.annotation.*;

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
