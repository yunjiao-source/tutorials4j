package tutorials4j.framework.feature.crypto.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.crypto.core.properties.CryptoProperties;
import tutorials4j.framework.feature.crypto.web.CryptoEndpoint;

/**
 * 功能配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_FEATURE, name = "crypto-enabled")
public class CryptoFeatureConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[FEATURE-CRYPTO] Crypto Feature Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CryptoEndpoint cryptoEndpoint(CryptoProperties properties) {
    log.trace("[FEATURE-CRYPTO] Crypto Endpoint");
    return new CryptoEndpoint(
        properties.getAsymmetricCryptoStrategy(), properties.getSymmetricCryptoStrategy());
  }
}
