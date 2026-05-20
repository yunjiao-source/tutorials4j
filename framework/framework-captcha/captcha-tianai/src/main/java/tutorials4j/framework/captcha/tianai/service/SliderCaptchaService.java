package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.TianAiCaptchaGenerateParamBuilder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class SliderCaptchaService extends AbstractCaptchaService {

  public SliderCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianAiCaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_SLIDER;
  }
}
