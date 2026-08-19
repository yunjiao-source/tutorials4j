package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * TOTP 动态口令认证注解。
 *
 * <p>标注在接口方法或类型上，要求请求携带 TOTP 用户名与认证码请求头，用于双因素认证校验。
 *
 * @author Yun Jiao
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TotpAuth {

  // 用户名请求头名称
  /**
   * 用户名请求头名称。
   *
   * @return 用户名请求头名称，默认取自 {@link DefaultConsts}
   */
  String userName() default DefaultConsts.HTTP_HEADER_TOTP_AUTH_USERNAME;

  // 认证代码请求头名称
  /**
   * 认证代码请求头名称。
   *
   * @return 认证代码请求头名称，默认取自 {@link DefaultConsts}
   */
  String authCode() default DefaultConsts.HTTP_HEADER_TOTP_AUTH_CODE;
}
