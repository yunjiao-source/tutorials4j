package tutorials4j.framework.captcha.hutool;

import cn.hutool.captcha.ShearCaptcha;

/**
 * 扭曲干扰验证码 创建器
 *
 * @author Yun Jiao
 */
public class ShearCaptchaBuilder extends AbstractCaptchaBuilder<ShearCaptcha> {
  @Override
  protected ShearCaptcha createCaptcha() {
    return new ShearCaptcha(width(), height(), generator(), interfereCount());
  }
}
