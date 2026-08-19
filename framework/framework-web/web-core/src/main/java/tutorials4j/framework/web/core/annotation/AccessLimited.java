package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 访问频率限制注解。
 *
 * <p>标注在接口方法上，限制该方法在指定时间窗口内的最大访问次数。
 *
 * @author Yun Jiao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AccessLimited {
  /**
   * 时间窗口内允许的最大访问次数。
   *
   * @return 最大访问次数，默认 3 次
   */
  int maxTimes() default 3;
}
