package tutorials4j.framework.web.logging.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.core.TaskDecoratorCreator;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;
import tutorials4j.framework.web.logging.mdc.TraceExchangeFilterFunction;
import tutorials4j.framework.web.logging.mdc.TraceRequestFilter;
import tutorials4j.framework.web.logging.mdc.TraceRestTemplateInterceptor;
import tutorials4j.framework.web.logging.mdc.TraceTaskDecorator;
import tutorials4j.framework.web.logging.properties.TraceWebProperties;

/**
 * 链路追踪（Trace）功能的自动配置类。
 *
 * <p>在配置属性 {@code PropertiesConsts.PROPERTY_PREFIX_WEB_TRACE} 对应的 enabled 开关开启时生效， 注册 MDC
 * 追踪上下文在异步任务、RestTemplate、RestClient、WebClient 以及 Servlet 请求间的传递组件。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_TRACE,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties(TraceWebProperties.class)
public class TraceWebConfiguration {
  /** 初始化日志输出，应用启动后执行。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-LOGGING] Trace Logging Configuration");
  }

  /**
   * 注册 {@link TaskDecoratorCreator}，用于在异步任务中传递 MDC 追踪上下文。
   *
   * @return 任务装饰器创建器
   */
  @Bean
  @ConditionalOnMissingBean
  TaskDecoratorCreator traceTaskDecoratorCreator() {
    log.trace("[WEB-LOGGING] Trace Task Decorator Creator");
    return TraceTaskDecorator::new;
  }

  /**
   * 注册 RestTemplate 追踪信息传递定制器。
   *
   * @return RestTemplate 定制器，用于添加追踪信息拦截器
   */
  @Bean
  RestTemplateCustomizer traceRestTemplateCustomizer() {
    log.trace("[WEB-LOGGING] Trace Rest Template Customizer");
    return restTemplate -> {
      restTemplate.getInterceptors().add(new TraceRestTemplateInterceptor());
    };
  }

  /**
   * 注册 RestClient 追踪信息传递定制器。
   *
   * @return RestClient 定制器，用于添加追踪信息拦截器
   */
  @Bean
  RestClientCustomizer traceRestClientCustomizer() {
    log.trace("[WEB-LOGGING] Trace Rest Client Customizer");
    return restClientBuilder -> {
      restClientBuilder.requestInterceptor(new TraceRestTemplateInterceptor());
    };
  }

  /**
   * 注册 WebClient 追踪信息传递定制器。
   *
   * @return WebClient 定制器，用于添加追踪信息过滤器
   */
  @Bean
  WebClientCustomizer traceWebClientCustomizer() {
    log.trace("[WEB-LOGGING] Trace Web Client Customizer");
    return webClientBuilder -> {
      webClientBuilder.filter(new TraceExchangeFilterFunction());
    };
  }

  /**
   * 注册链路追踪 Servlet 过滤器 {@link TraceRequestFilter}。
   *
   * @param properties 链路追踪配置属性，包含过滤器通用配置
   * @return 过滤器注册 Bean
   */
  @Bean
  FilterRegistrationBean<TraceRequestFilter> traceRequestFilterRegistration(
      TraceWebProperties properties) {
    ServletFilterOptions options = properties.getFilter();
    FilterRegistrationBean<TraceRequestFilter> registration = new FilterRegistrationBean<>();
    TraceRequestFilter filter = new TraceRequestFilter();
    registration.setFilter(filter);
    options.fill(registration);

    log.trace("[WEB-LOGGING] 'TraceRequestFilter' configuration parameters are {}", options);
    return registration;
  }
}
