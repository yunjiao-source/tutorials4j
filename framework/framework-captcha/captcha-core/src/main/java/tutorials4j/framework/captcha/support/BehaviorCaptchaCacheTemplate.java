package tutorials4j.framework.captcha.support;

import tutorials4j.framework.cache.core.NamedCacheConsts;
import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * 行为验证码缓存模板。
 *
 * <p>继承自多级缓存抽象模板，专门用于存储行为验证码的键值对（key 为验证码 ID，value 为验证码文本）。 缓存名称为 {@code captcha-behavior}。
 *
 * @author Yun Jiao
 */
public class BehaviorCaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {

  /** 构造行为验证码缓存模板，绑定缓存名称 {@code captcha-behavior}。 */
  public BehaviorCaptchaCacheTemplate() {
    super(NamedCacheConsts.CAPTCHA_BEHAVIOR);
  }

  /** 返回缓存值类型为 {@link String}。 */
  @Override
  public Class<String> getValueClass() {
    return String.class;
  }

  /** 行为验证码直接以键作为值返回。 */
  @Override
  public String valueGenerator(String key) {
    return key;
  }
}
