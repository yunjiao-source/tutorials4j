package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.*;

/**
 * 访问频率限制
 *
 * @author Yun Jiao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AccessLimited {
    int maxTimes() default 3;
}
