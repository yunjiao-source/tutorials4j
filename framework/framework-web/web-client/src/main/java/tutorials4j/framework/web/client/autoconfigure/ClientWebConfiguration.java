package tutorials4j.framework.web.client.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequest;
import tutorials4j.framework.web.client.ClientUtils;
import tutorials4j.framework.web.client.ClientWebProperties;
import tutorials4j.framework.web.client.ClientWebProperties.RetryOptions;
import tutorials4j.framework.web.client.LoggingClientHttpRequestInterceptor;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ClientWebProperties.class})
public class ClientWebConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.debug("[WEB-CLIENT] Web Client Configuration");
  }

  @Bean
  RestTemplateCustomizer logHeadersRestTemplateCustomizer() {
    log.debug("[WEB-CLIENT] Log Headers Rest Template Builder Customizer");
    return restTemplate -> {
      restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());
    };
  }

  @Bean
  RestTemplateRequestCustomizer<ClientHttpRequest> defaultHeadersRestTemplateRequestCustomizer(
      ClientWebProperties properties) {
    log.debug("[WEB-CLIENT] Default Headers Rest Template Request Customizer");
    return request -> {
      properties.getDefaultHeaders().forEach(request.getHeaders()::set);
    };
  }

  @Configuration(proxyBeanMethods = false)
  public static class RestClientConfiguration {
    @PostConstruct
    public void postConstruct() {
      log.debug("[WEB-CLIENT] Rest Client Configuration");
    }

    @Bean
    RestClientCustomizer logHeadersRestClientCustomizer() {
      log.debug("[WEB-CLIENT] Log Headers Rest Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.requestInterceptor(new LoggingClientHttpRequestInterceptor());
      };
    }

    @Bean
    RestClientCustomizer defaultHeadersRestClientCustomizer(ClientWebProperties properties) {
      log.debug("[WEB-CLIENT] Default Headers Rest Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.defaultHeaders(
            header -> properties.getDefaultHeaders().forEach(header::set));
      };
    }
  }

  @Configuration(proxyBeanMethods = false)
  public static class WebClientConfiguration {
    @PostConstruct
    public void postConstruct() {
      log.debug("[WEB-CLIENT] Web Client Configuration");
    }

    @Bean
    WebClientCustomizer defaultHeadersWebClientCustomizer(ClientWebProperties properties) {
      log.debug("[WEB-CLIENT] Default Headers Web Client Customizer");
      return webClientBuilder -> {
        webClientBuilder.defaultHeaders(
            header -> properties.getDefaultHeaders().forEach(header::set));
      };
    }

    @Bean
    WebClientCustomizer defaultWebClientCustomizer() {
      log.debug("[WEB-CLIENT] Default Web Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.filter(ClientUtils.ofCatchExcepitonLogger());
      };
    }

    @Bean
    WebClientCustomizer logHeadersWebClientCustomizer() {
      log.debug("[WEB-CLIENT] Log Headers Web Client Customizer");
      return webClientBuilder -> {
        webClientBuilder.filter(ClientUtils.ofClientRequestLogger());
        webClientBuilder.filter(ClientUtils.ofClientResponseLogger());
      };
    }

    @Bean
    WebClientCustomizer retryWebClientCustomizer(ClientWebProperties properties) {
      log.debug("[WEB-CLIENT] Retry Web Client Customizer");
      RetryOptions options = properties.getRetry();
      return webClientBuilder ->
          webClientBuilder.filter(
              ClientUtils.ofRetry(
                  options.getMaxAttempts(), options.getMinBackoff(), options.getMaxBackoff()));
    }
  }
}
