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
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CACHAED_BODY,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(CachedBodyWebProperties.class)
public class CachedBodyConfiguration {

  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-REST] Cached Body Configuration");
  }

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
