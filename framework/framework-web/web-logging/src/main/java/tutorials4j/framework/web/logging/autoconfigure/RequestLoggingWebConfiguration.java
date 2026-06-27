package tutorials4j.framework.web.logging.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.logging.SimpleRequestLoggingFilter;
import tutorials4j.framework.web.logging.properties.RequestLoggingWebProperties;
import tutorials4j.framework.web.logging.properties.RequestLoggingWebProperties.RequestOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_REQUEST_LOGGING,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(RequestLoggingWebProperties.class)
public class RequestLoggingWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-LOGGING] Request Logging Configuration");
  }

  /**
   * 注册请求日志过滤器的 {@link FilterRegistrationBean}。
   *
   * <p>根据 {@link RequestOptions} 创建并初始化 {@link SimpleRequestLoggingFilter}，同时应用其内部的过滤器配置（如启用状态、URL
   * 模式等）。 若过滤器启用，还会检查并确保对应日志级别可用。
   *
   * @param properties Web HTTP 配置属性，包含请求日志相关选项
   * @return 过滤器注册 Bean，用于将过滤器添加到 Servlet 容器
   */
  @Bean
  FilterRegistrationBean<SimpleRequestLoggingFilter> simpleRequestLoggingFilter(
      RequestLoggingWebProperties properties) {
    RequestOptions requestOptions = properties.getOptions();
    SimpleRequestLoggingFilter filter = new SimpleRequestLoggingFilter();
    filter.init(requestOptions);

    ServletFilterOptions servletFilterOptions = properties.getFilter();
    FilterRegistrationBean<SimpleRequestLoggingFilter> registration =
        new FilterRegistrationBean<>();
    registration.setFilter(filter);
    servletFilterOptions.fill(registration);

    if (registration.isEnabled()) {
      // 设置日志级别
      Logger logger = LoggerFactory.getLogger(CommonsRequestLoggingFilter.class.getName());
      logger.isEnabledForLevel(Level.DEBUG);
    }

    log.trace(
        "[WEB-LOGGING] 'SimpleRequestLoggingFilter' configuration parameters are {}",
        servletFilterOptions);
    return registration;
  }
}
