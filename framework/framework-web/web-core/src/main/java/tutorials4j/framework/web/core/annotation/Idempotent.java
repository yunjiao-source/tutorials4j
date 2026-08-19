package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 幂等性注解。
 *
 * <p>标注在接口方法上，用于标识该接口需要保证幂等性，避免重复提交造成的影响。
 *
 * @author Yun Jiao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Idempotent {}
