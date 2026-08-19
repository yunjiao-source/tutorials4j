package tutorials4j.framework.examples.template;

import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import tutorials4j.framework.cache.core.template.AbstractMultiLevelCacheTemplate;

/**
 * 基于多级缓存模板实现的验证码缓存服务，负责验证码的生成、缓存存储与校验。
 *
 * @author Yun Jiao
 */
@Service
public class CaptchaCacheTemplate extends AbstractMultiLevelCacheTemplate<String, String> {
  /** 构造器，指定缓存名为 captcha。 */
  public CaptchaCacheTemplate() {
    super("captcha");
  }

  /** {@inheritDoc} */
  @Override
  public Class<String> getValueClass() {
    return String.class;
  }

  /** {@inheritDoc} */
  @Override
  public String valueGenerator(String key) {
    return RandomStringUtils.secure().nextAlphanumeric(4);
  }

  /**
   * 校验用户输入的验证码是否与缓存中的值一致。
   *
   * @param key 验证码的缓存键
   * @param inputValue 用户输入的验证码
   * @return 一致返回 true，否则返回 false
   */
  public boolean check(String key, String inputValue) {
    String cacheValue = get(key);
    return Objects.equals(cacheValue, inputValue);
  }
}
