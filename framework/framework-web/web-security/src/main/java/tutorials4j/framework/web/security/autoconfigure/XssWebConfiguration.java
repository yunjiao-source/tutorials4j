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
 * XSS 防护功能自动配置类，在属性开启时注册 XSS 过滤器与 Jackson 序列化模块。
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
  /** 配置初始化完成后输出跟踪日志。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-SECURITY] Xss Web Configuration");
  }

  /** 注册用于 XSS 过滤的 Jackson 序列化模块 Bean。 */
  @Bean
  @Order(JacksonConsts.MODULE_ORDER_XSS)
  XssJacksonSimpleModule xssJacksonSimpleModule() {
    log.trace("[WEB-SECURITY] Xss Jackson Simple Module");
    return new XssJacksonSimpleModule();
  }

  /**
   * 注册 XSS 请求过滤器，并按配置填充过滤器的匹配规则。
   *
   * @param properties XSS 相关配置属性
   * @return XSS 请求过滤器的注册对象
   */
  @Bean
  FilterRegistrationBean<XssRequestFilter> xssRequestFilterRegistration(
      XssWebProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<XssRequestFilter> registration = new FilterRegistrationBean<>();
    XssRequestFilter filter = new XssRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);

    log.trace("[WEB-SECURITY] 'XssRequestFilter' configuration parameters are {}", options);
    return registration;
  }
}
