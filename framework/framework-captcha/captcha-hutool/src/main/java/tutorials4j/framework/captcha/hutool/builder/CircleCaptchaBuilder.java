package tutorials4j.framework.captcha.hutool.builder;

import cn.hutool.captcha.CircleCaptcha;

/**
 * 圆圈干扰验证码 创建器
 *
 * @author Yun Jiao
 */
public class CircleCaptchaBuilder extends AbstractCaptchaBuilder<CircleCaptcha> {

  /** 创建圆圈干扰验证码实例。 */
  @Override
  protected CircleCaptcha createCaptcha() {
    return new CircleCaptcha(width(), height(), generator(), interfereCount());
  }
}
