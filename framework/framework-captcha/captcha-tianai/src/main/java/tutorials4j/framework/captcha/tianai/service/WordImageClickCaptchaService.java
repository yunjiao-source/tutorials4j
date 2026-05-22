package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.CaptchaGenerateParamBuilder;

/**
 * 文字点选验证码服务。
 *
 * @author Yun Jiao
 */
public class WordImageClickCaptchaService extends AbstractCaptchaService {

  public WordImageClickCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, CaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_WORD_IMAGE_CLICK;
  }
}
