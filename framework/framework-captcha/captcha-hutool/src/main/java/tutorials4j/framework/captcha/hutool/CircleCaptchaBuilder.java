package tutorials4j.framework.captcha.hutool;

import cn.hutool.captcha.CircleCaptcha;

/**
 * 圆圈干扰验证码 创建器
 *
 * @author Yun Jiao
 */
public class CircleCaptchaBuilder extends AbstractCaptchaBuilder<CircleCaptcha> {

  @Override
  protected CircleCaptcha createCaptcha() {
    return new CircleCaptcha(width(), height(), generator(), interfereCount());
  }
}
