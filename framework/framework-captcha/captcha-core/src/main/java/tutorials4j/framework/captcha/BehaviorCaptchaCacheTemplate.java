package tutorials4j.framework.captcha;

import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class BehaviorCaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {

  public BehaviorCaptchaCacheTemplate() {
    super("behavior-captcha");
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
