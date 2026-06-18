package tutorials4j.framework.captcha.hutool.builder;

import cn.hutool.captcha.LineCaptcha;

/**
 * 线段干扰的验证码 创建器
 *
 * @author Yun Jiao
 */
public class LineCaptchaBuilder extends AbstractCaptchaBuilder<LineCaptcha> {
  @Override
  protected LineCaptcha createCaptcha() {
    return new LineCaptcha(width(), height(), generator(), interfereCount());
  }
}
