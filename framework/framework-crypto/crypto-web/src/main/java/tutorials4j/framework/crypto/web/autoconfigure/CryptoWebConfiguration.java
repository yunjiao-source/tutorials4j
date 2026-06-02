package tutorials4j.framework.crypto.web.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.crypto.core.processor.CryptoProcessor;
import tutorials4j.framework.crypto.core.processor.CryptoProcessorFactory;
import tutorials4j.framework.crypto.core.properties.CryptoProperties;
import tutorials4j.framework.crypto.web.CryptoEndpoint;
import tutorials4j.framework.crypto.web.CryptoRequestBodyAdvice;

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
  CryptoRequestBodyAdvice cryptoRequestBodyAdvice(CryptoProperties properties) {
    CryptoProcessor processor =
        CryptoProcessorFactory.instance.findProcessor(
            properties.getAsymmetricCryptoStrategy().getCategory());
    log.debug("[CRYPTO-WEB] Crypto Request Body Advice");
    return new CryptoRequestBodyAdvice(processor);
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoEndpoint cryptoEndpoint(CryptoProperties properties) {
    log.debug("[CRYPTO-WEB] Crypto Endpoint");
    return new CryptoEndpoint(properties);
  }
}
