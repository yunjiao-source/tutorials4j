package tutorials4j.framework.captcha.hutool;

import cn.hutool.captcha.ShearCaptcha;
import java.awt.image.BufferedImage;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.captcha.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.CaptchaCategory;

/**
 * 扭曲干扰验证码 服务
 *
 * @author Yun Jiao
 */
@Slf4j
public class ShearCaptchaService extends AbstractCaptchaService {
  private final ShearCaptchaBuilder builder;

  public ShearCaptchaService(
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate, ShearCaptchaBuilder builder) {
    super(behaviorCaptchaCacheTemplate);
    this.builder = builder;
  }

  @Override
  public Map<String, Object> draw() {
    ShearCaptcha captcha = builder.build();
    // 生成码
    String code = captcha.getGenerator().generate();
    // 生成图片
    BufferedImage image = (BufferedImage) captcha.createImage(code);

    return createCaptchaData(code, image).toMap();
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.HUTOOL_SHEAR;
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
