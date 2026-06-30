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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_CRYPTO_WEB,
    name = PropertiesConsts.PROPERTY_ENABLED)
public class CryptoWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[CRYPTO-WEB] Crypto Web Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoRequestBodyAdvice cryptoRequestBodyAdvice(
      CryptoProcessorCacheTemplate cryptoProcessorCacheTemplate) {
    log.trace("[CRYPTO-WEB] Crypto Request Body Advice");
    return new CryptoRequestBodyAdvice(cryptoProcessorCacheTemplate);
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoResponseBodyAdvice cryptoResponseBodyAdvice(
      CryptoProcessorCacheTemplate cryptoProcessorCacheTemplate) {
    log.trace("[CRYPTO-WEB] Crypto Response Body Advice");
    return new CryptoResponseBodyAdvice(cryptoProcessorCacheTemplate);
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoEndpoint cryptoEndpoint(CryptoProperties properties) {
    log.trace("[CRYPTO-WEB] Crypto Endpoint");
    return new CryptoEndpoint(
        properties.getAsymmetricCryptoStrategy(), properties.getSymmetricCryptoStrategy());
  }
}
