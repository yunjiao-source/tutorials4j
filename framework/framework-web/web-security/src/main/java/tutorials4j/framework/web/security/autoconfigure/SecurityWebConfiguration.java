package tutorials4j.framework.web.security.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.JacksonConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.security.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.security.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.security.cache.SignatureCacheTemplate;
import tutorials4j.framework.web.security.filter.XssRequestFilter;
import tutorials4j.framework.web.security.interceptor.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.security.interceptor.IdempotentHandlerInterceptor;
import tutorials4j.framework.web.security.interceptor.SignatureHandlerInterceptor;
import tutorials4j.framework.web.security.properties.GoogleWebProperties;
import tutorials4j.framework.web.security.properties.SecurityWebProperties;
import tutorials4j.framework.web.security.signature.SignatureKeyRepository;
import tutorials4j.framework.web.security.signature.SimpleSignatureKeyRepository;
import tutorials4j.framework.web.security.xss.XssJacksonSimpleModule;

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
  SignatureCacheTemplate signatureCacheTemplate() {
    log.debug("[WEB-SECURITY] Signature Cache Template");
    return new SignatureCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  SignatureKeyRepository simpleSignatureKeyRepository(SecurityWebProperties properties) {
    log.debug("[WEB-SECURITY] Simple Signature Key Repository");
    return new SimpleSignatureKeyRepository(properties.getSignature().getKeys());
  }

  @Bean
  @Order(JacksonConsts.MODULE_ORDER_XSS)
  XssJacksonSimpleModule xssJacksonSimpleModule() {
    log.debug("[WEB-SECURITY] Xss Jackson Simple Module");
    return new XssJacksonSimpleModule();
  }

  @Bean
  FilterRegistrationBean<XssRequestFilter> xssRequestFilterRegistration(
      SecurityWebProperties properties) {
    ServletFilterOptions options = properties.getXss();
    FilterRegistrationBean<XssRequestFilter> registration = new FilterRegistrationBean<>();
    XssRequestFilter filter = new XssRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);
    if (log.isDebugEnabled()) {
      log.debug("[WEB-SECURITY] Xss攻击过滤器：{}", options);
    }
    return registration;
  }

  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class InterceptorWebConfiguration implements WebMvcConfigurer {

    private final AccessLimitedCacheTemplate accessLimitedCacheTemplate;
    private final IdempotentCacheTemplate idempotentCacheTemplate;
    private final SignatureCacheTemplate signatureCacheTemplate;
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
          new SignatureHandlerInterceptor(signatureCacheTemplate, signatureKeyRepository);
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
