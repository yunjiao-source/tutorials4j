package tutorials4j.springboot3.webflux.httpexchange; // src/main/java/com/example/demo/config/HttpClientConfig.java

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

  @Bean
  public RestClient restClient() {
    return RestClient.builder().baseUrl("https://jsonplaceholder.typicode.com").build();
  }

  @Bean
  public UserApiClient userApiRestClient(RestClient restClient) {
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(adapter)
            .customArgumentResolver(new UserQueryArgumentResolver())
            .build();
    return factory.createClient(UserApiClient.class);
  }

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .baseUrl("https://jsonplaceholder.typicode.com")
        .filter(new DefaultHeaderExchangeFilterFunction())
        .filter(new RetryExchangeFilterFunction())
        .filter(new LoggingExchangeFilterFunction())
        .filter(new SecurityExchangeFilterFunction())
        .build();
  }

  @Bean
  public UserApiClient userApiWebClient(WebClient webClient) {
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
            .customArgumentResolver(new UserQueryArgumentResolver())
            .build();
    return factory.createClient(UserApiClient.class);
  }
}
