package tutorials4j.framework.crypto.web;

import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.cache.core.CacheNameConsts;
import tutorials4j.framework.cache.core.template.AbstractCaffeineCacheTemplate;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.SymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class CryptoRequestCacheTemplate
    extends AbstractCaffeineCacheTemplate<String, CryptoProcessor> {
  private final CryptoProcessor asymmetricProcessor;
  private final CryptoProcessor symmetricProcessor;

  public CryptoRequestCacheTemplate(
      AsymmetricCryptoStrategy asymmetricCryptoStrategy,
      SymmetricCryptoStrategy symmetricCryptoStrategy) {
    super(CacheNameConsts.CRYPTO_REQUEST);
    this.asymmetricProcessor =
        CryptoProcessorFactory.instance.findProcessor(asymmetricCryptoStrategy.getCategory());
    this.symmetricProcessor =
        CryptoProcessorFactory.instance.findProcessor(symmetricCryptoStrategy.getCategory());
  }

  @Override
  public Class<CryptoProcessor> getValueClass() {
    return CryptoProcessor.class;
  }

  @Override
  public CryptoProcessor valueGenerator(String key) {
    if (log.isDebugEnabled()) {
      log.debug("[CRYPTO-WEB] 创建新的对称加密处理器，key={}", key);
    }
    // 用非对称解密私钥
    String secretKey = asymmetricProcessor.decrypt(key);
    // 创建新的实例
    return symmetricProcessor.newInstance(new SecretKey(secretKey));
  }
}
