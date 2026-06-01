package tutorials4j.framework.web.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.rest.RestWebProperties;
import tutorials4j.framework.web.rest.cachedbody.CachedBodyRequestFilter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({RestWebProperties.class})
public class RestWebConfiguration {

  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-REST] Web Rest Configuration");
  }

  @Bean
  FilterRegistrationBean<CachedBodyRequestFilter> cachedBodyFilterRegistration(
      RestWebProperties properties) {
    ServletFilterOptions options = properties.getCachedBody();
    FilterRegistrationBean<CachedBodyRequestFilter> registration = new FilterRegistrationBean<>();
    CachedBodyRequestFilter filter = new CachedBodyRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);

    if (log.isDebugEnabled()) {
      log.debug("[WEB-REST] 缓存请求体过滤器：{}", options);
    }
    return registration;
  }
}
