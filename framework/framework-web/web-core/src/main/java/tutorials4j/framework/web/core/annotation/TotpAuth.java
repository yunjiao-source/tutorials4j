package tutorials4j.framework.web.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * totp认证
 *
 * @author Yun Jiao
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TotpAuth {

  // 用户名请求头名称
  String userName() default DefaultConsts.HTTP_HEADER_TOTP_AUTH_USERNAME;

  // 认证代码请求头名称
  String authCode() default DefaultConsts.HTTP_HEADER_TOTP_AUTH_CODE;
}
