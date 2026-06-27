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
 * TODO
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
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-LOGGING] Trace Logging Configuration");
  }

  @Bean
  @ConditionalOnMissingBean
  TaskDecoratorCreator traceTaskDecoratorCreator() {
    log.trace("[WEB-LOGGING] Trace Task Decorator Creator");
    return TraceTaskDecorator::new;
  }

  @Bean
  RestTemplateCustomizer traceRestTemplateCustomizer() {
    log.trace("[WEB-LOGGING] Trace Rest Template Customizer");
    return restTemplate -> {
      restTemplate.getInterceptors().add(new TraceRestTemplateInterceptor());
    };
  }

  @Bean
  RestClientCustomizer traceRestClientCustomizer() {
    log.trace("[WEB-LOGGING] Trace Rest Client Customizer");
    return restClientBuilder -> {
      restClientBuilder.requestInterceptor(new TraceRestTemplateInterceptor());
    };
  }

  @Bean
  WebClientCustomizer traceWebClientCustomizer() {
    log.trace("[WEB-LOGGING] Trace Web Client Customizer");
    return webClientBuilder -> {
      webClientBuilder.filter(new TraceExchangeFilterFunction());
    };
  }

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
