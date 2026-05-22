package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.CaptchaGenerateParamBuilder;

/**
 * 拼图验证码服务。
 *
 * @author Yun Jiao
 */
public class ConcatCaptchaService extends AbstractCaptchaService {

  public ConcatCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, CaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_CONCAT;
  }
}
