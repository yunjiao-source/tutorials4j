package tutorials4j.framework.examples.app;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@Profile("client")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.client"})
public class ClientConfig {

  @Bean
  RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  RestClient restClient(RestClient.Builder builder) {
    return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
  }

  @Bean
  WebClient webClient(WebClient.Builder builder) {
    return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
  }

  /**
   * 添加自定义请求头,如认证请求头
   *
   * @return
   */
  @Bean
  public RestTemplateRequestCustomizer<ClientHttpRequest> headerRestTemplateRequestCustomizer() {
    return request -> {
      log.info("headerRestTemplateRequestCustomizer - 添加自定义请求头,如认证请求头");
      request
          .getHeaders()
          .set("Authorization", "Bearer " + RandomStringUtils.secure().nextAlphabetic(12));
    };
  }

  @Bean
  RestClientCustomizer headersRestClientCustomizer() {
    return restClientBuilder -> {
      log.info("headersRestClientCustomizer - 添加自定义请求头,如认证请求头");
      restClientBuilder.defaultHeaders(
          header ->
              header.set(
                  "Authorization", "Bearer " + RandomStringUtils.secure().nextAlphabetic(12)));
    };
  }

  @Bean
  WebClientCustomizer headersWebClientCustomizer() {
    return webClientBuilder -> {
      log.info("headersWebClientCustomizer - 添加自定义请求头,如认证请求头");
      webClientBuilder.defaultHeaders(
          header ->
              header.set(
                  "Authorization", "Bearer " + RandomStringUtils.secure().nextAlphabetic(12)));
    };
  }
}
