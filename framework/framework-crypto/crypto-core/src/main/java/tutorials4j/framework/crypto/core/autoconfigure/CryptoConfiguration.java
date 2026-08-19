package tutorials4j.framework.crypto.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.crypto.core.bean.CryptoCategory;
import tutorials4j.framework.crypto.core.bean.DigestCategory;
import tutorials4j.framework.crypto.core.cache.CryptoProcessorCacheTemplate;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;
import tutorials4j.framework.crypto.core.processor.DigestProcessor;
import tutorials4j.framework.crypto.core.processor.DigestProcessorFactory;
import tutorials4j.framework.crypto.core.properties.CryptoProperties;
import tutorials4j.framework.crypto.core.properties.WebCryptoProperties;

/**
 * 加密核心自动配置类，负责注册加密处理器缓存模板、加密处理器工厂与摘要处理器工厂等核心 Bean。
 *
 * <p>通过 {@code @EnableConfigurationProperties} 启用 {@link CryptoProperties} 与 {@link
 * WebCryptoProperties} 配置绑定；从 Spring 容器中收集所有 {@link CryptoProcessor} / {@link DigestProcessor} 实现，
 * 并按类别注册到对应的处理器工厂单例中。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({CryptoProperties.class, WebCryptoProperties.class})
public class CryptoConfiguration {
  /** 初始化完成后输出一条 trace 日志，用于确认配置类已加载。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CRYPTO-CORE] Crypto Configuration");
  }

  /**
   * 创建加密处理器缓存模板 Bean，用于缓存按会话密钥创建的对称加密处理器。
   *
   * @param properties 加密配置属性
   * @param cryptoProcessorFactory 加密处理器工厂
   * @return 加密处理器缓存模板
   */
  @Bean
  @ConditionalOnMissingBean
  CryptoProcessorCacheTemplate cryptoRequestCacheTemplate(
      CryptoProperties properties, CryptoProcessorFactory cryptoProcessorFactory) {
    log.trace("[CRYPTO-CORE] Crypto Request Cache Template");
    return new CryptoProcessorCacheTemplate(
        cryptoProcessorFactory,
        properties.getAsymmetricCryptoStrategy(),
        properties.getSymmetricCryptoStrategy());
  }

  /**
   * 创建加密处理器工厂 Bean，收集容器中全部 {@link CryptoProcessor} 并按类别注册到工厂单例。
   *
   * @param providers 容器中可用的加密处理器提供者
   * @return 加密处理器工厂单例
   */
  @Bean
  @ConditionalOnMissingBean
  CryptoProcessorFactory cryptoProcessorFactory(ObjectProvider<CryptoProcessor> providers) {
    Map<CryptoCategory, CryptoProcessor> processors =
        providers.stream().collect(Collectors.toMap(CryptoProcessor::getCategory, m -> m));
    log.trace(
        "[CRYPTO-CORE] Injected instances in CryptoProcessorFactory is {}", processors.keySet());
    CryptoProcessorFactory.instance.setProcessors(processors);
    return CryptoProcessorFactory.instance;
  }

  /**
   * 创建摘要处理器工厂 Bean，收集容器中全部 {@link DigestProcessor} 并按类别注册到工厂单例。
   *
   * @param providers 容器中可用的摘要处理器提供者
   * @return 摘要处理器工厂单例
   */
  @Bean
  @ConditionalOnMissingBean
  DigestProcessorFactory digestProcessorFactory(ObjectProvider<DigestProcessor> providers) {
    Map<DigestCategory, DigestProcessor> processors =
        providers.stream().collect(Collectors.toMap(DigestProcessor::getCategory, m -> m));
    log.trace(
        "[CRYPTO-CORE] Injected instances in DigestProcessorFactory is {}", processors.keySet());
    DigestProcessorFactory.instance.setProcessors(processors);
    return DigestProcessorFactory.instance;
  }
}
