package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.support.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.support.CaptchaGenerateParamBuilder;

/**
 * 文字点选验证码服务。
 *
 * @author Yun Jiao
 */
public class WordImageClickCaptchaService extends AbstractCaptchaService {

  /**
   * 构造文字点选验证码服务。
   *
   * @param imageCaptchaApplication 图片验证码应用
   * @param builder 生成参数构建器
   */
  public WordImageClickCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, CaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  /** 返回验证码类别，固定为文字点选验证码。 */
  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_WORD_IMAGE_CLICK;
  }
}
