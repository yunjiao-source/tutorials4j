package tutorials4j.framework.captcha.hutool.service;

import cn.hutool.captcha.LineCaptcha;
import java.awt.image.BufferedImage;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.captcha.hutool.builder.LineCaptchaBuilder;
import tutorials4j.framework.captcha.support.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.support.CaptchaCategory;

/**
 * 线段干扰的验证码 服务
 *
 * @author Yun Jiao
 */
@Slf4j
public class LineCaptchaService extends AbstractCaptchaService {
  /** 线段干扰验证码构建器，提供验证码生成所需的配置 */
  private final LineCaptchaBuilder builder;

  /**
   * 构造线段干扰验证码服务。
   *
   * @param behaviorCaptchaCacheTemplate 验证码缓存操作模板
   * @param builder 线段干扰验证码构建器
   */
  public LineCaptchaService(
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate, LineCaptchaBuilder builder) {
    super(behaviorCaptchaCacheTemplate);
    this.builder = builder;
  }

  /** 生成线段干扰验证码，并缓存验证码答案。 */
  @Override
  public Map<String, Object> draw() {
    LineCaptcha captcha = builder.build();
    // 生成码
    String code = captcha.getGenerator().generate();
    // 生成图片
    BufferedImage image = (BufferedImage) captcha.createImage(code);

    return createCaptchaData(code, image).toMap();
  }

  /** 获取验证码分类为 Hutool 线段干扰验证码。 */
  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.HUTOOL_LINE;
  }

  /** 获取校验时是否忽略大小写的配置。 */
  @Override
  protected Boolean getValidIgnoreCase() {
    return builder.validIgnoreCase();
  }

  /** 获取图片模糊度配置。 */
  @Override
  protected Integer getFuzziness() {
    return builder.fuzziness();
  }
}
