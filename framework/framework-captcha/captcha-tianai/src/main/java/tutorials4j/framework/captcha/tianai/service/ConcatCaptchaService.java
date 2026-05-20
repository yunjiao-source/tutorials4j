package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.TianAiCaptchaGenerateParamBuilder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class ConcatCaptchaService extends AbstractCaptchaService {

  public ConcatCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, TianAiCaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_CONCAT;
  }
}
