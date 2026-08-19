package tutorials4j.framework.captcha.support;

import tutorials4j.framework.cache.core.NamedCacheConsts;
import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * 图形验证码缓存模板，继承自多级缓存模板。
 *
 * <p>用于存储图形验证码相关的临时数据（key 为验证码 ID，value 为验证码文本）， 缓存名称为 {@code captcha-graphic}。
 *
 * @author Yun Jiao
 */
public class GraphicCaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {

  /** 构造图形验证码缓存模板，绑定缓存名称 {@code captcha-graphic}。 */
  public GraphicCaptchaCacheTemplate() {
    super(NamedCacheConsts.CAPTCHA_GRAPHIC);
  }

  /** 返回缓存值类型为 {@link String}。 */
  @Override
  public Class<String> getValueClass() {
    return String.class;
  }

  /** 图形验证码直接以键作为值返回。 */
  @Override
  public String valueGenerator(String key) {
    return key;
  }
}
