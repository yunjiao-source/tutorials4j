package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 签名校验注解
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredSignature {
  /** 是否必填签名 */
  boolean required() default true;

  /** 时间窗口（秒） */
  long timeWindow() default 300;

  /** 是否验证 nonce */
  boolean checkNonce() default true;
}
