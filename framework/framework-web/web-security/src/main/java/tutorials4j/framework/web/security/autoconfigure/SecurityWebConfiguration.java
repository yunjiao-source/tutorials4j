package tutorials4j.framework.web.security.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.web.security.properties.SecurityWebProperties;
import tutorials4j.framework.web.security.rest.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.security.rest.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.security.rest.IdempotentCacheTemplate;
import tutorials4j.framework.web.security.rest.IdempotentHandlerInterceptor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_SECURITY,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(SecurityWebProperties.class)
public class SecurityWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Security Web Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  AccessLimitedCacheTemplate accessLimitedCacheTemplate() {
    log.trace("[WEB-SECURITY] Access Limited Cache Template");
    return new AccessLimitedCacheTemplate();
  }

  @Bean
  @ConditionalOnMissingBean
  IdempotentCacheTemplate idempotentCacheTemplate() {
    log.trace("[WEB-SECURITY] Idempotent Cache Template");
    return new IdempotentCacheTemplate();
  }

  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class SecurityWebMvcConfigurer implements WebMvcConfigurer {

    private final AccessLimitedCacheTemplate accessLimitedCacheTemplate;
    private final IdempotentCacheTemplate idempotentCacheTemplate;
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
    }

    private void doAddInterceptor(
        InterceptorRegistry registry,
        HandlerInterceptor interceptor,
        HandlerInterceptorOptions options) {
      InterceptorRegistration registration = registry.addInterceptor(interceptor);
      if (options.getExcludePathPatterns().length > 0) {
        registration.excludePathPatterns(options.getExcludePathPatterns());
      }

      if (options.getIncludePathPatterns().length > 0) {
        registration.addPathPatterns(options.getIncludePathPatterns());
      }

      log.trace("[WEB-SECURITY] 添加请求拦截器：{}, {}", interceptor, options);
    }
  }
}
