package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 签名校验注解。
 *
 * <p>标注在接口方法上，要求请求携带合法的签名请求头，并可按需校验时间窗口与 nonce。
 *
 * @author Yun Jiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredSignature {
  /**
   * 是否强制要求签名。
   *
   * @return 为 {@code true} 时签名必填，默认 {@code true}
   */
  boolean required() default true;

  /**
   * 签名时间窗口（秒），超出窗口的请求视为无效。
   *
   * @return 时间窗口秒数，默认 300 秒
   */
  long timeWindowSeconds() default 300;

  /**
   * 是否校验 nonce，防止请求重放。
   *
   * @return 为 {@code true} 时校验 nonce，默认 {@code true}
   */
  boolean checkNonce() default true;
}
