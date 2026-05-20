package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.TianAiCaptchaGenerateParamBuilder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class RotateCaptchaService extends AbstractCaptchaService {
  public RotateCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianAiCaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_ROTATE;
  }
}
