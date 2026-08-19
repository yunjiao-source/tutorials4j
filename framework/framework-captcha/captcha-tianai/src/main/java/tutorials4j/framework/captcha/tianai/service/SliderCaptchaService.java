package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.support.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.support.CaptchaGenerateParamBuilder;

/**
 * 滑动验证码服务。
 *
 * @author Yun Jiao
 */
public class SliderCaptchaService extends AbstractCaptchaService {

  /**
   * 构造滑动验证码服务。
   *
   * @param imageCaptchaApplication 图片验证码应用
   * @param builder 生成参数构建器
   */
  public SliderCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, CaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  /** 返回验证码类别，固定为滑动验证码。 */
  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_SLIDER;
  }
}
