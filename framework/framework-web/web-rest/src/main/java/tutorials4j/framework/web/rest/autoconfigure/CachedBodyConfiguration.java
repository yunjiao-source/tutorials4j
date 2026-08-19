package tutorials4j.framework.web.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.rest.cachedbody.CachedBodyRequestFilter;
import tutorials4j.framework.web.rest.properties.CachedBodyWebProperties;

/**
 * 请求体缓存自动配置类。
 *
 * <p>当配置项 {@code tutorials4j.web.cached-body.enabled=true} 时生效，注册 {@link CachedBodyRequestFilter}
 * 过滤器并应用过滤参数，将请求体缓存起来供后续重复读取。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CACHED_BODY,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(CachedBodyWebProperties.class)
public class CachedBodyConfiguration {

  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-REST] Cached Body Configuration");
  }

  /**
   * 注册请求体缓存过滤器的 FilterRegistrationBean，并按配置填充过滤参数。
   *
   * @param properties 请求体缓存 Web 配置属性
   * @return 请求体缓存过滤器的注册信息
   */
  @Bean
  FilterRegistrationBean<CachedBodyRequestFilter> cachedBodyFilterRegistration(
      CachedBodyWebProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<CachedBodyRequestFilter> registration = new FilterRegistrationBean<>();
    CachedBodyRequestFilter filter = new CachedBodyRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);

    log.trace("[WEB-REST] 'CachedBodyRequestFilter' configuration parameters are {}", options);
    return registration;
  }
}
