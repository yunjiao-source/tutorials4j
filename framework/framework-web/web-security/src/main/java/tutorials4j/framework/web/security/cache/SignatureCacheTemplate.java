package tutorials4j.framework.web.security.cache;

import cn.hutool.core.util.IdUtil;
import tutorials4j.framework.cache.core.template.AbstractRedisCacheTemplate;

/**
 * 签名防重放缓存模板。
 *
 * <p>用于存储已使用的 nonce，以防止重放攻击。底层使用 Redis 缓存，键值对形式存储， 值由 {@link #valueGenerator(String)} 自动生成唯一标识。
 *
 * @author Yun Jiao
 */
public class SignatureCacheTemplate extends AbstractRedisCacheTemplate<String, String> {
  public SignatureCacheTemplate() {
    super("signature");
  }

  @Override
  public Class<String> getValueClass() {
    return String.class;
  }

  @Override
  public String valueGenerator(String key) {
    return IdUtil.fastSimpleUUID();
  }
}
