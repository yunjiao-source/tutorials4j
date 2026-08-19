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
 * 客户端 Web 组件（RestTemplate / RestClient / WebClient）的自动配置类。
 *
 * <p>在配置属性 {@code PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT} 对应的 enabled 开关开启时生效， 为
 * RestTemplate、RestClient 和 WebClient 注册请求/响应日志记录、默认请求头设置以及重试等定制器。
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
  /** 初始化日志输出，应用启动后执行。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[WEB-CLIENT] Client Web Configuration");
  }

  /**
   * 注册 RestTemplate 请求/响应日志记录定制器。
   *
   * @return RestTemplate 定制器，用于添加日志记录拦截器
   */
  @Bean
  RestTemplateCustomizer logHeadersRestTemplateCustomizer() {
    log.trace("[WEB-CLIENT] Log Headers Rest Template Builder Customizer");
    return restTemplate -> {
      restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor());
    };
  }

  /**
   * 注册为 RestTemplate 请求设置默认请求头的定制器。
   *
   * @param properties 客户端 Web 配置属性，包含默认请求头信息
   * @return RestTemplate 请求定制器
   */
  @Bean
  RestTemplateRequestCustomizer<ClientHttpRequest> defaultHeadersRestTemplateRequestCustomizer(
      ClientWebProperties properties) {
    log.trace("[WEB-CLIENT] Default Headers Rest Template Request Customizer");
    return request -> {
      properties.getDefaultHeaders().forEach(request.getHeaders()::set);
    };
  }

  /** RestClient 客户端定制器的自动配置类。 */
  @Configuration(proxyBeanMethods = false)
  public static class RestClientWebConfiguration {
    /** 初始化日志输出，应用启动后执行。 */
    @PostConstruct
    public void postConstruct() {
      log.trace("[WEB-CLIENT] Rest Client Configuration");
    }

    /**
     * 注册 RestClient 请求/响应日志记录定制器。
     *
     * @return RestClient 定制器，用于添加日志记录拦截器
     */
    @Bean
    RestClientCustomizer logHeadersRestClientCustomizer() {
      log.trace("[WEB-CLIENT] Log Headers Rest Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.requestInterceptor(new LoggingClientHttpRequestInterceptor());
      };
    }

    /**
     * 注册为 RestClient 设置默认请求头的定制器。
     *
     * @param properties 客户端 Web 配置属性，包含默认请求头信息
     * @return RestClient 定制器
     */
    @Bean
    RestClientCustomizer defaultHeadersRestClientCustomizer(ClientWebProperties properties) {
      log.trace("[WEB-CLIENT] Default Headers Rest Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.defaultHeaders(
            header -> properties.getDefaultHeaders().forEach(header::set));
      };
    }
  }

  /** WebClient 客户端定制器的自动配置类。 */
  @Configuration(proxyBeanMethods = false)
  public static class WebClientWebConfiguration {
    /** 初始化日志输出，应用启动后执行。 */
    @PostConstruct
    public void postConstruct() {
      log.trace("[WEB-CLIENT] Web Client Configuration");
    }

    /**
     * 注册为 WebClient 设置默认请求头的定制器。
     *
     * @param properties 客户端 Web 配置属性，包含默认请求头信息
     * @return WebClient 定制器
     */
    @Bean
    WebClientCustomizer defaultHeadersWebClientCustomizer(ClientWebProperties properties) {
      log.trace("[WEB-CLIENT] Default Headers Web Client Customizer");
      return webClientBuilder -> {
        webClientBuilder.defaultHeaders(
            header -> properties.getDefaultHeaders().forEach(header::set));
      };
    }

    /**
     * 注册 WebClient 默认定制器，添加异常捕获日志过滤器。
     *
     * @return WebClient 定制器
     */
    @Bean
    WebClientCustomizer defaultWebClientCustomizer() {
      log.trace("[WEB-CLIENT] Default Web Client Customizer");
      return restClientBuilder -> {
        restClientBuilder.filter(ClientUtils.ofCatchExcepitonLogger());
      };
    }

    /**
     * 注册 WebClient 请求/响应日志记录定制器。
     *
     * @return WebClient 定制器
     */
    @Bean
    WebClientCustomizer logHeadersWebClientCustomizer() {
      log.trace("[WEB-CLIENT] Log Headers Web Client Customizer");
      return webClientBuilder -> {
        webClientBuilder.filter(ClientUtils.ofClientRequestLogger());
        webClientBuilder.filter(ClientUtils.ofClientResponseLogger());
      };
    }

    /**
     * 注册 WebClient 重试定制器，按配置的尝试次数与退避时间进行重试。
     *
     * @param properties 客户端 Web 配置属性，包含重试相关选项
     * @return WebClient 定制器
     */
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
