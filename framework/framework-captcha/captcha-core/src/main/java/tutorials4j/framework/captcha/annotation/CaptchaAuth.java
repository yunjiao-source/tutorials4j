package tutorials4j.framework.captcha.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tutorials4j.framework.common.core.DefaultConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CaptchaAuth {
  String key() default DefaultConsts.HTTP_HEADER_CAPTCHA_KEY;

  String category() default DefaultConsts.HTTP_HEADER_CAPTCHA_CATEGORY;

  String code() default DefaultConsts.HTTP_HEADER_CAPTCHA_CODE;
}
