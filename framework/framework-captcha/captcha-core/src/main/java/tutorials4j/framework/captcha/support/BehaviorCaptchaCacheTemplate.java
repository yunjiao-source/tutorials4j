package tutorials4j.framework.captcha.support;

import tutorials4j.framework.cache.core.CacheNameConsts;
import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * 行为验证码缓存模板。
 *
 * <p>继承自多级缓存抽象模板，专门用于存储验证码的键值对（key为验证码ID，value为验证码文本）。 缓存名称为"behavior-captcha"。
 *
 * @author Yun Jiao
 */
public class BehaviorCaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {

  public BehaviorCaptchaCacheTemplate() {
    super(CacheNameConsts.CAPTCHA_BEHAVIOR);
  }

  @Override
  public Class<String> getValueClass() {
    return String.class;
  }

  @Override
  public String valueGenerator(String key) {
    return key;
  }
}
