package tutorials4j.framework.captcha;

import tutorials4j.framework.cache.core.CacheNameConsts;
import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * 图形验证码缓存模板，继承自多级缓存模板。
 *
 * <p>用于存储验证码相关的临时数据。
 *
 * @author Yun Jiao
 */
public class GraphicCaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {

  public GraphicCaptchaCacheTemplate() {
    super(CacheNameConsts.CAPTCHA_GRAPHIC);
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
