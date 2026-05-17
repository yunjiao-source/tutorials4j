package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import tutorials4j.framework.common.core.support.ServletFilterOptions;
import tutorials4j.framework.web.core.properties.FilterWebProperties;
import tutorials4j.framework.web.mvc.filter.CachedBodyRequestFilter;
import tutorials4j.framework.web.mvc.filter.DefaultCommonsRequestLoggingFilter;
import tutorials4j.framework.web.mvc.filter.XssRequestFilter;

/**
 * 缓存请求体配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class FilterWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-MVC] Filter Web Configuration");
  }

  @Bean
  FilterRegistrationBean<XssRequestFilter> xssRequestFilterRegistration(
      FilterWebProperties properties) {
    ServletFilterOptions options = properties.getXss();
    FilterRegistrationBean<XssRequestFilter> registration = new FilterRegistrationBean<>();
    XssRequestFilter filter = new XssRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);
    if (log.isDebugEnabled()) {
      log.debug("[WEB-MVC] Xss攻击过滤器：{}", options);
    }
    return registration;
  }

  @Bean
  FilterRegistrationBean<CachedBodyRequestFilter> cachedBodyFilterRegistration(
      FilterWebProperties properties) {
    ServletFilterOptions options = properties.getCachedBody();
    FilterRegistrationBean<CachedBodyRequestFilter> registration = new FilterRegistrationBean<>();
    CachedBodyRequestFilter filter = new CachedBodyRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);

    if (log.isDebugEnabled()) {
      log.debug("[WEB-MVC] 缓存请求体过滤器：{}", options);
    }
    return registration;
  }

  /**
   * 注册请求日志过滤器的 {@link FilterRegistrationBean}。
   *
   * <p>根据 {@link FilterWebProperties.RequestLoggingOptions} 创建并初始化 {@link
   * DefaultCommonsRequestLoggingFilter}，同时应用其内部的过滤器配置（如启用状态、URL 模式等）。 若过滤器启用，还会检查并确保对应日志级别可用。
   *
   * @param properties Web HTTP 配置属性，包含请求日志相关选项
   * @return 过滤器注册 Bean，用于将过滤器添加到 Servlet 容器
   */
  @Bean
  FilterRegistrationBean<DefaultCommonsRequestLoggingFilter>
      defaultCommonsRequestLoggingFilterRegistration(FilterWebProperties properties) {
    FilterWebProperties.RequestLoggingOptions options = properties.getRequestLogging();
    ServletFilterOptions servletFilterOptions = options.getFilter();

    DefaultCommonsRequestLoggingFilter filter = new DefaultCommonsRequestLoggingFilter(options);
    filter.init();

    FilterRegistrationBean<DefaultCommonsRequestLoggingFilter> registration =
        new FilterRegistrationBean<>();
    registration.setFilter(filter);
    servletFilterOptions.fill(registration);

    if (registration.isEnabled()) {
      // 设置日志级别
      Logger logger = LoggerFactory.getLogger(CommonsRequestLoggingFilter.class.getName());
      logger.isEnabledForLevel(Level.DEBUG);
    }

    if (log.isDebugEnabled()) {
      log.debug("[WEB-MVC] 请求日志过滤器：{}", options);
    }
    return registration;
  }
}
