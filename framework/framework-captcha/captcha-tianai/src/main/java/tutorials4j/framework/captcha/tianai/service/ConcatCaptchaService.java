package tutorials4j.framework.captcha.tianai.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import tutorials4j.framework.captcha.support.CaptchaCategory;
import tutorials4j.framework.captcha.tianai.support.CaptchaGenerateParamBuilder;

/**
 * 拼图验证码服务。
 *
 * @author Yun Jiao
 */
public class ConcatCaptchaService extends AbstractCaptchaService {

  /**
   * 构造拼图验证码服务。
   *
   * @param imageCaptchaApplication 图片验证码应用
   * @param builder 生成参数构建器
   */
  public ConcatCaptchaService(
      ImageCaptchaApplication imageCaptchaApplication, CaptchaGenerateParamBuilder builder) {
    super(imageCaptchaApplication, builder);
  }

  /** 返回验证码类别，固定为拼图验证码。 */
  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.TIANAI_CONCAT;
  }
}
