package tutorials4j.framework.crypto.core.cache;

import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.cache.core.NamedCacheConsts;
import tutorials4j.framework.cache.core.template.AbstractCaffeineCacheTemplate;
import tutorials4j.framework.common.core.bean.SecretKey;
import tutorials4j.framework.crypto.core.bean.AsymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.bean.SymmetricCryptoStrategy;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;

/**
 * 基于 Caffeine 本地缓存的加密处理器缓存模板，专门用于缓存 {@link CryptoProcessor} 实例。
 *
 * <p><b>核心功能</b>：将经过非对称加密的对称密钥（密文）作为缓存键（Key），将对应的对称加密处理器（Value）缓存在本地内存中。
 * 当缓存未命中时，自动使用非对称私钥解密缓存键得到明文对称密钥，并创建一个新的 {@link CryptoProcessor} 实例存入缓存。
 *
 * <p><b>工作流程</b>：
 *
 * <ol>
 *   <li>调用方传入一个加密后的对称密钥字符串（通常为 Base64 编码的 RSA 加密密文）。
 *   <li>若该键已存在于 Caffeine 缓存中，直接返回对应的 {@link CryptoProcessor} 实例。
 *   <li>若缓存未命中，则执行 {@link #valueGenerator(String)}：
 *       <ul>
 *         <li>使用注入的 {@link #asymmetricProcessor}（非对称处理器）对键进行解密，还原出明文的对称密钥。
 *         <li>使用注入的 {@link #symmetricProcessor}（对称处理器工厂）基于明文密钥创建新的 {@link CryptoProcessor} 实例。
 *         <li>将新实例存入缓存并返回。
 *       </ul>
 * </ol>
 *
 * <p><b>设计意图</b>：
 *
 * <ul>
 *   <li><b>安全</b>：缓存键仅为密文，明文的对称密钥不会在内存中长时间暴露，仅在处理器实例内部持有。
 *   <li><b>性能</b>：通过本地缓存复用已创建的处理器实例，避免重复解密和对象创建开销，尤其适用于多租户、多密钥的高频加解密场景。
 *   <li><b>解耦</b>：将缓存策略与加密策略分离，利用 Spring 依赖注入灵活配置非对称/对称算法。
 * </ul>
 *
 * <p><b>缓存配置</b>：底层缓存实例的名称为 {@link NamedCacheConsts#CRYPTO_PROCESSOR}，具体过期策略、最大容量等由 Caffeine
 * 配置决定（通常通过 {@link tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory} 统一管理）。
 *
 * <p><b>线程安全</b>：本类是无状态的（仅持有处理器工厂实例），且父类 {@link
 * tutorials4j.framework.cache.core.template.AbstractCacheTemplate} 对缓存获取做了双重检查锁，因此可安全地在多线程环境中使用。
 *
 * @author Yun Jiao
 * @see CryptoProcessor
 * @see AsymmetricCryptoStrategy
 * @see SymmetricCryptoStrategy
 * @see CryptoProcessorFactory
 * @see AbstractCaffeineCacheTemplate
 */
@Slf4j
public class CryptoProcessorCacheTemplate
    extends AbstractCaffeineCacheTemplate<String, CryptoProcessor> {
  /** 非对称加密处理器（用于解密缓存键），通常持有 RSA 私钥。 */
  private final CryptoProcessor asymmetricProcessor;

  /** 对称加密处理器工厂（用于创建新的对称处理器实例），通常为 AES 等算法。 */
  private final CryptoProcessor symmetricProcessor;

  /**
   * 构造一个新的 {@code CryptoExchangeCacheTemplate} 实例。
   *
   * <p>构造过程中会从 {@link CryptoProcessorFactory} 中根据策略的 {@code category} 分别获取非对称和对称处理器。 缓存名称自动设为
   * {@link NamedCacheConsts#CRYPTO_PROCESSOR}。
   *
   * @param cryptoProcessorFactory 加密处理器工厂，用于根据策略类别查找具体的处理器实现
   * @param asymmetricCryptoStrategy 非对称加密策略配置（如 RSA），其 {@code category} 用于查找对应的非对称处理器
   * @param symmetricCryptoStrategy 对称加密策略配置（如 AES），其 {@code category} 用于查找对应的对称处理器工厂
   * @throws IllegalArgumentException 若工厂中找不到对应策略的处理器时抛出（由工厂实现决定）
   */
  public CryptoProcessorCacheTemplate(
      CryptoProcessorFactory cryptoProcessorFactory,
      AsymmetricCryptoStrategy asymmetricCryptoStrategy,
      SymmetricCryptoStrategy symmetricCryptoStrategy) {
    super(NamedCacheConsts.CRYPTO_PROCESSOR);
    this.asymmetricProcessor =
        cryptoProcessorFactory.findProcessor(asymmetricCryptoStrategy.getCategory());
    this.symmetricProcessor =
        cryptoProcessorFactory.findProcessor(symmetricCryptoStrategy.getCategory());
  }

  @Override
  public Class<CryptoProcessor> getValueClass() {
    return CryptoProcessor.class;
  }

  @Override
  public CryptoProcessor valueGenerator(String key) {
    // 用非对称解密私钥
    String secretKey = asymmetricProcessor.decrypt(key);

    if (log.isDebugEnabled()) {
      log.debug("创建对称算法处理器, category = {}", symmetricProcessor.getCategory());
    }
    // 创建新的实例
    return symmetricProcessor.newInstance(new SecretKey(secretKey));
  }
}
