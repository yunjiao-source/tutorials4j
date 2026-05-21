package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.JacksonConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.web.core.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.core.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.core.cache.SignatureCacheTemplate;
import tutorials4j.framework.web.core.properties.InterceptorWebProperties;
import tutorials4j.framework.web.core.support.SignatureKeyRepository;
import tutorials4j.framework.web.mvc.interceptor.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.mvc.interceptor.IdempotentHandlerInterceptor;
import tutorials4j.framework.web.mvc.interceptor.SignatureHandlerInterceptor;
import tutorials4j.framework.web.mvc.support.XssJacksonSimpleModule;

/**
 * 安全配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class InterceptorWebConfiguration implements WebMvcConfigurer {
  @Autowired private AccessLimitedCacheTemplate accessLimitedCacheTemplate;
  @Autowired private IdempotentCacheTemplate idempotentCacheTemplate;
  @Autowired private SignatureCacheTemplate signatureCacheTemplate;
  @Autowired private SignatureKeyRepository signatureKeyRepository;
  @Autowired private InterceptorWebProperties properties;

  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-MVC] Interceptor Web Configuration");
  }

  @Bean
  @Order(JacksonConsts.MODULE_ORDER_XSS)
  XssJacksonSimpleModule xssJacksonSimpleModule() {
    log.debug("[WEB-MVC] Xss Jackson Simple Module");
    return new XssJacksonSimpleModule();
  }

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
          "[WEB-MVC] 请求拦截器'{}' 未配置，原因是'include-path-patterns'或‘exclude-path-patterns’没有设置值",
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

    log.debug("[WEB-MVC] 添加请求拦截器：{}, {}", interceptor, options);
  }
}
