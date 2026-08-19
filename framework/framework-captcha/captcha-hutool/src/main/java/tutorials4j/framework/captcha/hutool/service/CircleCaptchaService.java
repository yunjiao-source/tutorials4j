package tutorials4j.framework.captcha.hutool.service;

import cn.hutool.captcha.CircleCaptcha;
import java.awt.image.BufferedImage;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.captcha.hutool.builder.CircleCaptchaBuilder;
import tutorials4j.framework.captcha.support.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.support.CaptchaCategory;

/**
 * 圆圈干扰验证码 服务
 *
 * @author Yun Jiao
 */
@Slf4j
public class CircleCaptchaService extends AbstractCaptchaService {
  /** 圆圈干扰验证码构建器，提供验证码生成所需的配置 */
  private final CircleCaptchaBuilder builder;

  /**
   * 构造圆圈干扰验证码服务。
   *
   * @param behaviorCaptchaCacheTemplate 验证码缓存操作模板
   * @param builder 圆圈干扰验证码构建器
   */
  public CircleCaptchaService(
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate, CircleCaptchaBuilder builder) {
    super(behaviorCaptchaCacheTemplate);
    this.builder = builder;
  }

  /** 生成圆圈干扰验证码，并缓存验证码答案。 */
  @Override
  public Map<String, Object> draw() {
    CircleCaptcha captcha = builder.build();
    // 生成码
    String code = captcha.getGenerator().generate();
    // 生成图片
    BufferedImage image = (BufferedImage) captcha.createImage(code);

    return createCaptchaData(code, image).toMap();
  }

  /** 获取验证码分类为 Hutool 圆圈干扰验证码。 */
  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.HUTOOL_CIRCLE;
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
