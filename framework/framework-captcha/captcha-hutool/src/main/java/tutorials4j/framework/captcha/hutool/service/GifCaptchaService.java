package tutorials4j.framework.captcha.hutool.service;

import cn.hutool.captcha.GifCaptcha;
import cn.hutool.core.util.IdUtil;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.captcha.hutool.bean.CaptchaData;
import tutorials4j.framework.captcha.hutool.builder.GifCaptchaBuilder;
import tutorials4j.framework.captcha.support.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.support.CaptchaCategory;

/**
 * gif验证码 服务
 *
 * @author Yun Jiao
 */
@Slf4j
public class GifCaptchaService extends AbstractCaptchaService {
  /** Gif 验证码构建器，提供验证码生成所需的配置 */
  private final GifCaptchaBuilder builder;

  /**
   * 构造 Gif 验证码服务。
   *
   * @param behaviorCaptchaCacheTemplate 验证码缓存操作模板
   * @param builder Gif 验证码构建器
   */
  public GifCaptchaService(
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate, GifCaptchaBuilder builder) {
    super(behaviorCaptchaCacheTemplate);
    this.builder = builder;
  }

  /** 生成 Gif 验证码，并缓存验证码答案。 */
  @Override
  public Map<String, Object> draw() {
    GifCaptcha captcha = builder.build();
    String code = captcha.getCode();
    String key = IdUtil.fastSimpleUUID();

    captchaCacheTemplate.put(key, code);
    CaptchaData captchaData =
        new CaptchaData()
            .key(key)
            .code(code)
            .category(getCategory())
            .captchaImage(captcha.getImageBytes());
    return captchaData.toMap();
  }

  /** 获取验证码分类为 Hutool Gif 验证码。 */
  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.HUTOOL_GIF;
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
