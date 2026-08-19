package tutorials4j.framework.captcha.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * 验证码校验注解，标注在方法或类型上以启用验证码校验。
 *
 * <p>通过注解属性指定校验所需的请求头名称，运行时校验器据此完成验证码验证。
 *
 * @author Yun Jiao
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CaptchaAuth {
  /** 验证码唯一键对应的请求头名称。 */
  String key() default DefaultConsts.HTTP_HEADER_CAPTCHA_KEY;

  /** 验证码分类对应的请求头名称。 */
  String category() default DefaultConsts.HTTP_HEADER_CAPTCHA_CATEGORY;

  /** 验证码值对应的请求头名称。 */
  String code() default DefaultConsts.HTTP_HEADER_CAPTCHA_CODE;
}
