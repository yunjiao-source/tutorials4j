package tutorials4j.framework.web.security.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import tutorials4j.framework.common.core.JacksonConsts;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.security.properties.XssWebProperties;
import tutorials4j.framework.web.security.xss.XssJacksonSimpleModule;
import tutorials4j.framework.web.security.xss.XssRequestFilter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_XSS,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(XssWebProperties.class)
public class XssWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Xss Web Configuration");
  }

  @Bean
  @Order(JacksonConsts.MODULE_ORDER_XSS)
  XssJacksonSimpleModule xssJacksonSimpleModule() {
    log.trace("[WEB-SECURITY] Xss Jackson Simple Module");
    return new XssJacksonSimpleModule();
  }

  @Bean
  FilterRegistrationBean<XssRequestFilter> xssRequestFilterRegistration(
      XssWebProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<XssRequestFilter> registration = new FilterRegistrationBean<>();
    XssRequestFilter filter = new XssRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);

    log.trace("[WEB-SECURITY] XssRequestFilter configuration parameters are {}", options);
    return registration;
  }
}
