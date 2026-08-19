package tutorials4j.framework.examples.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import tutorials4j.framework.web.flux.ClientUtils;

/**
 * 客户端 HTTP 工具配置：注册 {@link RestTemplate}、{@link RestClient} 与 {@link WebClient} 等 Bean，
 * 并为它们统一配置自定义请求头（如认证请求头）。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
public class Config {

  /**
   * 注册 {@link RestTemplate} Bean，用于同步阻塞方式的 HTTP 调用。
   *
   * @param builder RestTemplate 构建器
   * @return 构建完成的 RestTemplate
   */
  @Bean
  RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  /**
   * 注册 {@link RestClient} Bean，并配置统一的 baseUrl 指向 jsonplaceholder 测试接口。
   *
   * @param builder RestClient 构建器
   * @return 构建完成的 RestClient
   */
  @Bean
  RestClient restClient(RestClient.Builder builder) {
    return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
  }

  /**
   * 注册 {@link WebClient} Bean，并配置统一的 baseUrl 指向 jsonplaceholder 测试接口。
   *
   * @param builder WebClient 构建器
   * @return 构建完成的 WebClient
   */
  @Bean
  WebClient webClient(WebClient.Builder builder) {
    return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
  }

  /**
   * 创建 {@link RestTemplate} 请求自定义器，为每个请求添加自定义请求头（如认证请求头）。
   *
   * @return RestTemplate 请求自定义器
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

  /**
   * 创建 {@link RestClient} 自定义器，为 RestClient 的所有请求添加默认请求头（如认证请求头）。
   *
   * @return RestClient 自定义器
   */
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

  /**
   * 创建 {@link WebClient} 自定义器，为 WebClient 添加认证过滤器。
   *
   * @return WebClient 自定义器
   */
  @Bean
  WebClientCustomizer headersWebClientCustomizer() {
    return webClientBuilder -> {
      log.info("headersWebClientCustomizer - 添加认证请求头");
      webClientBuilder.filter(ClientUtils.ofAuth());
    };
  }
}
