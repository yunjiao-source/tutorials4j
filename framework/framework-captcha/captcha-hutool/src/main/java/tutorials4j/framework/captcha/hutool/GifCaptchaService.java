package tutorials4j.framework.captcha.hutool;

import cn.hutool.captcha.GifCaptcha;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.captcha.BehaviorCaptchaCacheTemplate;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaData;

/**
 * gif验证码 服务
 *
 * @author Yun Jiao
 */
@Slf4j
public class GifCaptchaService extends AbstractCaptchaService {
  private final GifCaptchaBuilder builder;

  public GifCaptchaService(
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate, GifCaptchaBuilder builder) {
    super(behaviorCaptchaCacheTemplate);
    this.builder = builder;
  }

  @Override
  public CaptchaData draw() {
    GifCaptcha captcha = builder.build();
    String code = captcha.getCode();
    String key = IdUtil.fastSimpleUUID();

    captchaCacheTemplate.put(key, code);
    return new CaptchaData()
        .key(key)
        .code(code)
        .category(getCategory())
        .captchaImage(captcha.getImageBytes());
  }

  @Override
  public CaptchaCategory getCategory() {
    return CaptchaCategory.HUTOOL_GIF;
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
