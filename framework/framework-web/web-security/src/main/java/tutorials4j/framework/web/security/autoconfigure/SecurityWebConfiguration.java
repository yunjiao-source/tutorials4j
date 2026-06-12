package tutorials4j.framework.web.security.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.web.security.properties.GoogleWebProperties;
import tutorials4j.framework.web.security.properties.SecurityWebProperties;
import tutorials4j.framework.web.security.rest.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.security.rest.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.security.rest.IdempotentCacheTemplate;
import tutorials4j.framework.web.security.rest.IdempotentHandlerInterceptor;
import tutorials4j.framework.web.security.signature.InMemerySignatureKeyRepository;
import tutorials4j.framework.web.security.signature.SignatureHandlerInterceptor;
import tutorials4j.framework.web.security.signature.SignatureKeyRepository;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({SecurityWebProperties.class, GoogleWebProperties.class})
public class SecurityWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-SECURITY] Web Security Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  AccessLimitedCacheTemplate accessLimitedCacheTemplate() {
    log.debug("[WEB-SECURITY] Access Limited Cache Template");
    return new AccessLimitedCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  IdempotentCacheTemplate idempotentCacheTemplate() {
    log.debug("[WEB-SECURITY] Idempotent Cache Template");
    return new IdempotentCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  SignatureKeyRepository inMemerySignatureKeyRepository(SecurityWebProperties properties) {
    log.debug("[WEB-SECURITY] In Memery Signature Key Repository");
    return new InMemerySignatureKeyRepository(properties);
  }

  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class InterceptorWebConfiguration implements WebMvcConfigurer {

    private final AccessLimitedCacheTemplate accessLimitedCacheTemplate;
    private final IdempotentCacheTemplate idempotentCacheTemplate;
    private final SignatureKeyRepository signatureKeyRepository;
    private final SecurityWebProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      HandlerInterceptorOptions accessLimitedOptions = properties.getAccessLimited();
      AccessLimitedHandlerInterceptor accessLimitedHandlerInterceptor =
          new AccessLimitedHandlerInterceptor(accessLimitedCacheTemplate);
      doAddInterceptor(registry, accessLimitedHandlerInterceptor, accessLimitedOptions);

      HandlerInterceptorOptions idempotentOptions = properties.getIdempotent();
      IdempotentHandlerInterceptor idempotentHandlerInterceptor =
          new IdempotentHandlerInterceptor(idempotentCacheTemplate);
      doAddInterceptor(registry, idempotentHandlerInterceptor, idempotentOptions);

      HandlerInterceptorOptions signatureOptions = properties.getSignature().getInterceptor();
      SignatureHandlerInterceptor signatureHandlerInterceptor =
          new SignatureHandlerInterceptor(
              properties.getSignature().getNonceRedisKeyPrefix(), signatureKeyRepository);
      doAddInterceptor(registry, signatureHandlerInterceptor, signatureOptions);
    }

    private void doAddInterceptor(
        InterceptorRegistry registry,
        HandlerInterceptor interceptor,
        HandlerInterceptorOptions options) {
      if (options.getExcludePathPatterns().length == 0
          && options.getIncludePathPatterns().length == 0) {
        log.warn(
            "[WEB-SECURITY] 请求拦截器'{}' 未配置，原因是'include-path-patterns'或‘exclude-path-patterns’没有设置值",
            interceptor);
        return;
      }

      InterceptorRegistration registration = registry.addInterceptor(interceptor);
      if (options.getExcludePathPatterns().length > 0) {
        registration.excludePathPatterns(options.getExcludePathPatterns());
      }

      if (options.getIncludePathPatterns().length > 0) {
        registration.addPathPatterns(options.getIncludePathPatterns());
      }

      log.debug("[WEB-SECURITY] 添加请求拦截器：{}, {}", interceptor, options);
    }
  }
}
