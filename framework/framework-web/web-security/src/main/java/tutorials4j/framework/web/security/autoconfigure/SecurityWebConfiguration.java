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
 * Web 安全功能自动配置类，在属性开启时注册访问限制与幂等相关的缓存模板及拦截器。
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
  /** 配置初始化完成后输出跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Security Web Configuration");
  }

  /** 注册访问限制缓存模板 Bean。 */
  @Bean
  @ConditionalOnMissingBean
  AccessLimitedCacheTemplate accessLimitedCacheTemplate() {
    log.trace("[WEB-SECURITY] Access Limited Cache Template");
    return new AccessLimitedCacheTemplate();
  }

  /** 注册幂等缓存模板 Bean。 */
  @Bean
  @ConditionalOnMissingBean
  IdempotentCacheTemplate idempotentCacheTemplate() {
    log.trace("[WEB-SECURITY] Idempotent Cache Template");
    return new IdempotentCacheTemplate();
  }

  /** Web 安全拦截器注册配置，按属性配置注册访问限制与幂等拦截器。 */
  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @RequiredArgsConstructor
  public static class SecurityWebMvcConfigurer implements WebMvcConfigurer {

    private final AccessLimitedCacheTemplate accessLimitedCacheTemplate;
    private final IdempotentCacheTemplate idempotentCacheTemplate;
    private final SecurityWebProperties properties;

    /**
     * 注册访问限制与幂等两个拦截器到拦截器注册表。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      HandlerInterceptorOptions accessLimitedOptions = properties.getAccessLimited();
      AccessLimitedHandlerInterceptor accessLimitedHandlerInterceptor =
          new AccessLimitedHandlerInterceptor(accessLimitedCacheTemplate);
      doAddInterceptor(registry, accessLimitedHandlerInterceptor, accessLimitedOptions);
      log.trace(
          "[WEB-SECURITY] 'AccessLimitedHandlerInterceptor' configuration parameters are {}",
          accessLimitedOptions);

      HandlerInterceptorOptions idempotentOptions = properties.getIdempotent();
      IdempotentHandlerInterceptor idempotentHandlerInterceptor =
          new IdempotentHandlerInterceptor(idempotentCacheTemplate);
      doAddInterceptor(registry, idempotentHandlerInterceptor, idempotentOptions);
      log.trace(
          "[WEB-SECURITY] 'IdempotentHandlerInterceptor' configuration parameters are {}",
          idempotentOptions);
    }

    /**
     * 按选项配置将拦截器添加到注册表，处理排除与包含的路径规则。
     *
     * @param registry 拦截器注册表
     * @param interceptor 拦截器实例
     * @param options 拦截器路径选项
     */
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
    }
  }
}
