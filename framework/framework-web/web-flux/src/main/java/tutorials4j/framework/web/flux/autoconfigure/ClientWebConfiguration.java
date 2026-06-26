package tutorials4j.framework.web.flux.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequest;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.web.flux.ClientUtils;
import tutorials4j.framework.web.flux.component.LoggingClientHttpRequestInterceptor;
import tutorials4j.framework.web.flux.properties.ClientWebProperties;
import tutorials4j.framework.web.flux.properties.ClientWebProperties.RetryOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,
    name = PropertiesConsts.PROPERTY_ENABLED)
@EnableConfigurationProperties({ClientWebProperties.class})
public class ClientWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-CLIENT] Client Web Configuration");
  }

  @Bean
  RestTemplateCustomizer logHeadersRestTemplateCustomizer() {
    log.trace("[WEB-CLIENT] Log Headers Rest Template Builder Customizer");
    return restTemplate -> {
      restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());
    };
  }

  @Bean
  RestTemplateRequestCustomizer<ClientHttpRequest> defaultHeadersRestTemplateRequestCustomizer(
      ClientWebProperties properties) {
    log.trace("[WEB-CLIENT] Default Headers Rest Template Request Customizer");
    return request -> {
      properties.getDefaultHeaders().forEach(request.getHeaders()::set);
    };
  }

  @Configuration(proxyBeanMethods = false)
  public static class RestClientWebConfiguration {
    @PostConstruct
    public void postConstruct() {
      log.trace("[WEB-CLIENT] Rest Client Configuration");
    }

    @Bean
    RestClientCustomizer logHeadersRestClientCustomizer() {
      log.trace("[WEB-CLIENT] Log Headers Rest Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.requestInterceptor(new LoggingClientHttpRequestInterceptor());
      };
    }

    @Bean
    RestClientCustomizer defaultHeadersRestClientCustomizer(ClientWebProperties properties) {
      log.trace("[WEB-CLIENT] Default Headers Rest Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.defaultHeaders(
            header -> properties.getDefaultHeaders().forEach(header::set));
      };
    }
  }

  @Configuration(proxyBeanMethods = false)
  public static class WebClientWebConfiguration {
    @PostConstruct
    public void postConstruct() {
      log.trace("[WEB-CLIENT] Web Client Configuration");
    }

    @Bean
    WebClientCustomizer defaultHeadersWebClientCustomizer(ClientWebProperties properties) {
      log.trace("[WEB-CLIENT] Default Headers Web Client Customizer");
      return webClientBuilder -> {
        webClientBuilder.defaultHeaders(
            header -> properties.getDefaultHeaders().forEach(header::set));
      };
    }

    @Bean
    WebClientCustomizer defaultWebClientCustomizer() {
      log.trace("[WEB-CLIENT] Default Web Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.filter(ClientUtils.ofCatchExcepitonLogger());
      };
    }

    @Bean
    WebClientCustomizer logHeadersWebClientCustomizer() {
      log.trace("[WEB-CLIENT] Log Headers Web Client Customizer");
      return webClientBuilder -> {
        webClientBuilder.filter(ClientUtils.ofClientRequestLogger());
        webClientBuilder.filter(ClientUtils.ofClientResponseLogger());
      };
    }

    @Bean
    WebClientCustomizer retryWebClientCustomizer(ClientWebProperties properties) {
      log.trace("[WEB-CLIENT] Retry Web Client Customizer");
      RetryOptions options = properties.getRetry();
      return webClientBuilder ->
          webClientBuilder.filter(
              ClientUtils.ofRetry(
                  options.getMaxAttempts(), options.getMinBackoff(), options.getMaxBackoff()));
    }
  }
}
