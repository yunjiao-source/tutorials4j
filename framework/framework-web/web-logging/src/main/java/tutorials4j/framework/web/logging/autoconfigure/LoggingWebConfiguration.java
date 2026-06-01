package tutorials4j.framework.web.logging.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import tutorials4j.framework.common.spring.core.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.spring.core.TaskDecoratorCreator;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.logging.LoggingWebProperties;
import tutorials4j.framework.web.logging.LoggingWebProperties.RequestOptions;
import tutorials4j.framework.web.logging.SimpleRequestLoggingFilter;
import tutorials4j.framework.web.logging.mdc.TraceExchangeFilterFunction;
import tutorials4j.framework.web.logging.mdc.TraceRequestFilter;
import tutorials4j.framework.web.logging.mdc.TraceRestTemplateInterceptor;
import tutorials4j.framework.web.logging.mdc.TraceTaskDecorator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({LoggingWebProperties.class})
public class LoggingWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-LOGGING] Web Logging Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  CompositeTaskDecorator compositeTaskDecorator(CompositeTaskDecoratorCreator creator) {
    log.debug("[WEB-REST] Composite Task Decorator");
    return creator.getInstance();
  }

  @Bean
  @ConditionalOnMissingBean
  TaskDecoratorCreator traceTaskDecoratorCreator() {
    log.debug("[WEB-REST] Trace Task Decorator Creator");
    return TraceTaskDecorator::new;
  }

  @Bean
  RestTemplateCustomizer traceRestTemplateCustomizer() {
    log.debug("[WEB-REST] Trace Rest Template Customizer");
    return restTemplate -> {
      restTemplate.getInterceptors().add(new TraceRestTemplateInterceptor());
    };
  }

  @Bean
  RestClientCustomizer traceRestClientCustomizer() {
    log.debug("[WEB-REST] Trace Rest Client Customizer");
    return restClientBuilder -> {
      restClientBuilder.requestInterceptor(new TraceRestTemplateInterceptor());
    };
  }

  @Bean
  WebClientCustomizer traceWebClientCustomizer() {
    log.debug("[WEB-REST] Trace Web Client Customizer");
    return webClientBuilder -> {
      webClientBuilder.filter(new TraceExchangeFilterFunction());
    };
  }

  /**
   * 注册请求日志过滤器的 {@link FilterRegistrationBean}。
   *
   * <p>根据 {@link LoggingWebProperties.RequestOptions} 创建并初始化 {@link
   * SimpleRequestLoggingFilter}，同时应用其内部的过滤器配置（如启用状态、URL 模式等）。 若过滤器启用，还会检查并确保对应日志级别可用。
   *
   * @param properties Web HTTP 配置属性，包含请求日志相关选项
   * @return 过滤器注册 Bean，用于将过滤器添加到 Servlet 容器
   */
  @Bean
  FilterRegistrationBean<SimpleRequestLoggingFilter> defaultCommonsRequestLoggingFilterRegistration(
      LoggingWebProperties properties) {
    RequestOptions options = properties.getRequest();
    SimpleRequestLoggingFilter filter = new SimpleRequestLoggingFilter(options);
    filter.init();

    ServletFilterOptions servletFilterOptions = options.getFilter();
    FilterRegistrationBean<SimpleRequestLoggingFilter> registration =
        new FilterRegistrationBean<>();
    registration.setFilter(filter);
    servletFilterOptions.fill(registration);

    if (registration.isEnabled()) {
      // 设置日志级别
      Logger logger = LoggerFactory.getLogger(CommonsRequestLoggingFilter.class.getName());
      logger.isEnabledForLevel(Level.DEBUG);
    }

    if (log.isDebugEnabled()) {
      log.debug("[WEB-LOGGING] 请求日志过滤器：{}", servletFilterOptions);
    }
    return registration;
  }

  @Bean
  FilterRegistrationBean<TraceRequestFilter> traceRequestFilterRegistration(
      LoggingWebProperties properties) {
    ServletFilterOptions options = properties.getTrace();
    FilterRegistrationBean<TraceRequestFilter> registration = new FilterRegistrationBean<>();
    TraceRequestFilter filter = new TraceRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);

    if (log.isDebugEnabled()) {
      log.debug("[WEB-LOGGING] 跟踪信息过滤器：{}", options);
    }
    return registration;
  }
}
