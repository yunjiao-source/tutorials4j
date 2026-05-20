package tutorials4j.framework.captcha;

import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class GraphicCaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {

  public GraphicCaptchaCacheTemplate() {
    super("graphic-captcha");
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
