package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.*;

/**
 * 幂等性
 *
 * @author Yun Jiao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Idempotent {}
