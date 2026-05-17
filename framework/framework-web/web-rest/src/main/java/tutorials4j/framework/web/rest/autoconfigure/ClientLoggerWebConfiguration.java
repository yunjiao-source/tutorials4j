package tutorials4j.framework.web.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.rest.interceptor.LogClientHttpRequestInterceptor;
import tutorials4j.framework.web.rest.util.RestUtils;

/**
 * rest 客户端日志配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ClientLoggerWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-REST] Rest Client Logger Configuration");
  }

  @Bean
  RestTemplateCustomizer logHeadersRestTemplateCustomizer() {
    log.debug("[WEB-REST] Log Headers Rest Template Builder Customizer");
    return restTemplate -> {
      restTemplate.getInterceptors().add(new LogClientHttpRequestInterceptor());
    };
  }

  @Bean
  RestClientCustomizer logHeadersRestClientCustomizer() {
    log.debug("[WEB-REST] Log Headers Rest Client Customizer");
    return restClientBuilder -> {
      restClientBuilder.requestInterceptor(new LogClientHttpRequestInterceptor());
    };
  }

  @Bean
  WebClientCustomizer logHeadersWebClientCustomizer() {
    log.debug("[WEB-REST] Log Headers Web Client Customizer");
    return webClientBuilder -> {
      webClientBuilder.filter(RestUtils.ofClientRequestLogger());
      webClientBuilder.filter(RestUtils.ofClientResponseLogger());
    };
  }
}
