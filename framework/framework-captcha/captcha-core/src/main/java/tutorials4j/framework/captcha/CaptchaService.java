package tutorials4j.framework.captcha;

import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * 验证码服务接口
 *
 * @author Yun Jiao
 */
public abstract class CaptchaService {
  protected final AbstractMultiLevelCacheTemplate<String, String> captchaCacheTemplate;

  protected CaptchaService(AbstractMultiLevelCacheTemplate<String, String> captchaCacheTemplate) {
    this.captchaCacheTemplate = captchaCacheTemplate;
  }

  public abstract CaptchaData draw();

  public abstract boolean verify(String key, String userCode);

  public abstract CaptchaCategory getCategory();
}
