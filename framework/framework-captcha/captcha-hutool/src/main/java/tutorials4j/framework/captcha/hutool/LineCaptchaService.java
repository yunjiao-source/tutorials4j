package tutorials4j.framework.captcha.hutool;

import cn.hutool.captcha.LineCaptcha;
import java.awt.image.BufferedImage;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.captcha.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaData;

/**
 * 线段干扰的验证码 服务
 *
 * @author Yun Jiao
 */
@Slf4j
public class LineCaptchaService extends AbstractCaptchaService {
  private final LineCaptchaBuilder builder;

  public LineCaptchaService(
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate, LineCaptchaBuilder builder) {
    super(behaviorCaptchaCacheTemplate);
    this.builder = builder;
  }

  @Override
  public CaptchaData draw() {
    LineCaptcha captcha = builder.build();
    // 生成码
    String code = captcha.getGenerator().generate();
    // 生成图片
    BufferedImage image = (BufferedImage) captcha.createImage(code);

    return createCaptchaData(code, image);
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.HUTOOL_LINE;
  }

  @Override
  protected Boolean getValidIgnoreCase() {
    return builder.validIgnoreCase();
  }

  @Override
  protected Integer getFuzziness() {
    return builder.fuzziness();
  }
}
