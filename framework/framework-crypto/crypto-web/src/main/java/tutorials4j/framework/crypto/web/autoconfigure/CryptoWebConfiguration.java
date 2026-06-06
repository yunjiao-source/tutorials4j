package tutorials4j.framework.crypto.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;
import tutorials4j.framework.crypto.core.properties.CryptoProperties;
import tutorials4j.framework.crypto.web.CryptoRequestBodyAdvice;
import tutorials4j.framework.crypto.web.CryptoRequestCacheTemplate;
import tutorials4j.framework.crypto.web.CryptoResponseBodyAdvice;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CryptoWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CRYPTO-WEB] Crypto Web Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoRequestCacheTemplate cryptoRequestCacheTemplate(
      CryptoProperties properties, CryptoProcessorFactory cryptoProcessorFactory) {
    log.debug("[CRYPTO-WEB] Crypto Request Cache Template");
    return new CryptoRequestCacheTemplate(
        cryptoProcessorFactory,
        properties.getAsymmetricCryptoStrategy(),
        properties.getSymmetricCryptoStrategy());
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoRequestBodyAdvice cryptoRequestBodyAdvice(
      CryptoRequestCacheTemplate cryptoRequestCacheTemplate) {
    log.debug("[CRYPTO-WEB] Crypto Request Body Advice");
    return new CryptoRequestBodyAdvice(cryptoRequestCacheTemplate);
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoResponseBodyAdvice cryptoResponseBodyAdvice(
      CryptoRequestCacheTemplate cryptoRequestCacheTemplate) {
    log.debug("[CRYPTO-WEB] Crypto Response Body Advice");
    return new CryptoResponseBodyAdvice(cryptoRequestCacheTemplate);
  }
}
