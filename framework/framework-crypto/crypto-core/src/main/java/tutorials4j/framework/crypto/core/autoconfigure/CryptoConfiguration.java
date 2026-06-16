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

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({CryptoProperties.class})
public class CryptoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[CRYPTO-CORE] Crypto Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoProcessorCacheTemplate cryptoRequestCacheTemplate(
      CryptoProperties properties, CryptoProcessorFactory cryptoProcessorFactory) {
    log.debug("[CRYPTO-CORE] Crypto Request Cache Template");
    return new CryptoProcessorCacheTemplate(
        cryptoProcessorFactory,
        properties.getAsymmetricCryptoStrategy(),
        properties.getSymmetricCryptoStrategy());
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoProcessorFactory cryptoProcessorFactory(ObjectProvider<CryptoProcessor> providers) {
    Map<CryptoCategory, CryptoProcessor> processors =
        providers.stream().collect(Collectors.toMap(CryptoProcessor::getCategory, m -> m));
    log.debug("[CRYPTO-CORE] 工厂'CryptoProcessorFactory'注入实例：{}", processors);
    CryptoProcessorFactory.instance.setProcessors(processors);
    return CryptoProcessorFactory.instance;
  }

  @Bean
  @ConditionalOnMissingBean
  DigestProcessorFactory digestProcessorFactory(ObjectProvider<DigestProcessor> providers) {
    Map<DigestCategory, DigestProcessor> processors =
        providers.stream().collect(Collectors.toMap(DigestProcessor::getCategory, m -> m));
    log.debug("[CRYPTO-CORE] 工厂'DigestProcessorFactory'注入实例：{}", processors);
    DigestProcessorFactory.instance.setProcessors(processors);
    return DigestProcessorFactory.instance;
  }
}
