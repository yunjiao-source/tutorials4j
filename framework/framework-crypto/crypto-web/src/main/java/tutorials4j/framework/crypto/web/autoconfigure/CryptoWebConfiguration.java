package tutorials4j.framework.crypto.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.crypto.core.cache.CryptoProcessorCacheTemplate;
import tutorials4j.framework.crypto.core.properties.CryptoProperties;
import tutorials4j.framework.crypto.web.advice.CryptoRequestBodyAdvice;
import tutorials4j.framework.crypto.web.advice.CryptoResponseBodyAdvice;
import tutorials4j.framework.crypto.web.endpoint.CryptoEndpoint;

/**
 * 加密 Web 自动配置类，在 Web 层启用请求/响应加解密能力。
 *
 * <p>通过 {@code @ConditionalOnProperty} 根据 {@code crypto.web.enabled} 配置决定是否生效， 生效时注册请求体解密、响应体加密的
 * Advice 以及加密端点（Endpoint）。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_CRYPTO_WEB,
    name = PropertiesConsts.PROPERTY_ENABLED)
public class CryptoWebConfiguration {
  /** 初始化完成后输出一条 trace 日志，用于确认配置类已加载。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[CRYPTO-WEB] Crypto Web Configuration");
  }

  /**
   * 创建请求体解密增强器 Bean，用于解密标注 {@code @Crypto} 接口的加密请求体。
   *
   * @param cryptoProcessorCacheTemplate 加密处理器缓存模板
   * @return 请求体解密增强器
   */
  @Bean
  @ConditionalOnMissingBean
  CryptoRequestBodyAdvice cryptoRequestBodyAdvice(
      CryptoProcessorCacheTemplate cryptoProcessorCacheTemplate) {
    log.trace("[CRYPTO-WEB] Crypto Request Body Advice");
    return new CryptoRequestBodyAdvice(cryptoProcessorCacheTemplate);
  }

  /**
   * 创建响应体加密增强器 Bean，用于加密标注 {@code @Crypto} 接口的响应体。
   *
   * @param cryptoProcessorCacheTemplate 加密处理器缓存模板
   * @return 响应体加密增强器
   */
  @Bean
  @ConditionalOnMissingBean
  CryptoResponseBodyAdvice cryptoResponseBodyAdvice(
      CryptoProcessorCacheTemplate cryptoProcessorCacheTemplate) {
    log.trace("[CRYPTO-WEB] Crypto Response Body Advice");
    return new CryptoResponseBodyAdvice(cryptoProcessorCacheTemplate);
  }

  /**
   * 创建加密端点 Bean，用于对外提供加解密测试等管理能力。
   *
   * @param properties 加密配置属性
   * @return 加密端点
   */
  @Bean
  @ConditionalOnMissingBean
  CryptoEndpoint cryptoEndpoint(CryptoProperties properties) {
    log.trace("[CRYPTO-WEB] Crypto Endpoint");
    return new CryptoEndpoint(
        properties.getAsymmetricCryptoStrategy(), properties.getSymmetricCryptoStrategy());
  }
}
