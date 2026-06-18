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
  private final GifCaptchaBuilder builder;

  public GifCaptchaService(
      BehaviorCaptchaCacheTemplate behaviorCaptchaCacheTemplate, GifCaptchaBuilder builder) {
    super(behaviorCaptchaCacheTemplate);
    this.builder = builder;
  }

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
