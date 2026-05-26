package tutorials4j.framework.web.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tutorials4j.framework.web.core.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.core.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.core.cache.SignatureCacheTemplate;
import tutorials4j.framework.web.core.properties.ClientWebProperties;
import tutorials4j.framework.web.core.properties.FilterWebProperties;
import tutorials4j.framework.web.core.properties.GoogleAuthWebProperties;
import tutorials4j.framework.web.core.properties.InterceptorWebProperties;
import tutorials4j.framework.web.core.support.SignatureKeyRepository;
import tutorials4j.framework.web.core.support.SimpleSignatureKeyRepository;

/**
 * web core 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  FilterWebProperties.class,
  ClientWebProperties.class,
  InterceptorWebProperties.class,
  GoogleAuthWebProperties.class
})
@Import({ValidatorsWebConfiguration.class})
public class WebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-CORE] Web Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  AccessLimitedCacheTemplate accessLimitedCacheTemplate() {
    log.debug("[WEB-CORE] Access Limited Cache Template");
    return new AccessLimitedCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  IdempotentCacheTemplate idempotentCacheTemplate() {
    log.debug("[WEB-CORE] Idempotent Cache Template");
    return new IdempotentCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  SignatureCacheTemplate signatureCacheTemplate() {
    log.debug("[WEB-CORE] Signature Cache Template");
    return new SignatureCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  SignatureKeyRepository simpleSignatureKeyRepository(InterceptorWebProperties properties) {
    log.debug("[WEB-CORE] Simple Signature Key Repository");
    return new SimpleSignatureKeyRepository(properties.getSignature().getKeys());
  }
}
