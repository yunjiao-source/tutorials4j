package tutorials4j.framework.web.client;

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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import tutorials4j.framework.common.core.condition.ConditionalOnMapProperty;
import tutorials4j.framework.web.core.WebClientFrameworkException;
import tutorials4j.framework.web.core.WebPropertiesConsts;

import java.util.ArrayList;
import java.util.List;

/**
 * web client 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WebClientProperties.class)
public class WebClientConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Web Client Configuration");
    }



    @Configuration(proxyBeanMethods = false)
    static class RestTempalteConfiguration {
        @Bean
        @ConditionalOnMapProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "default-headers")
        RestTemplateRequestCustomizer<ClientHttpRequest> defaultHeadersRestTemplateRequestCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j |- Default Headers Rest Template Request Customizer");
            return request -> {
                properties.getDefaultHeaders().forEach(request.getHeaders()::set);
            };
        }

        @Bean
        @ConditionalOnProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "logger-enabled", havingValue = "true")
        RestTemplateCustomizer logHeadersRestTemplateCustomizer() {
            log.debug("Tutorials4j |- Log Headers Rest Template Builder Customizer");
            return restTemplate -> {
                restTemplate.getInterceptors().add(new LogHeaderClientHttpRequestInterceptor());
            };
        }

        @Bean
        @ConditionalOnProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "buffering-client-http-request-enabled", havingValue = "true")
        RestTemplateCustomizer bufferingClientHttpRequestTemplateBuilderCustomizer() {
            log.debug("Tutorials4j |- Buffering Client Http Request Template Builder Customizer");
            return restTemplate -> {
                // 特殊处理：避免拦截器执行两次
                List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
                restTemplate.getInterceptors().clear();

                ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
                if (!(factory instanceof BufferingClientHttpRequestFactory)) {
                    restTemplate.setInterceptors(interceptors);
                    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));
                }
            };
        }

    }


    @Configuration(proxyBeanMethods = false)
    static class RestClientConfiguration {
        @Bean
        @ConditionalOnProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "logger-enabled", havingValue = "true")
        RestClientCustomizer logHeadersRestClientCustomizer() {
            log.debug("Tutorials4j |- Log Headers Rest Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.requestInterceptor(new LogHeaderClientHttpRequestInterceptor());
            };
        }

        @Bean
        @ConditionalOnMapProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "default-headers")
        RestClientCustomizer defaultHeadersRestClientCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j |- Default Headers Rest Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.defaultHeaders(header -> properties.getDefaultHeaders().forEach(header::set));
            };
        }

        @Bean
        @ConditionalOnProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "base-url")
        RestClientCustomizer BaseUrlRestClientCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j |- Base Url Rest Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.baseUrl(properties.getBaseUrl());
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class SpringWebClientConfiguration {
        @Bean
        @ConditionalOnProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "base-url")
        WebClientCustomizer BaseUrlWebClientCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j |- Base Url Web Client Customizer");
            return webClientBuilder -> {
                webClientBuilder.baseUrl(properties.getBaseUrl());
            };
        }

        @Bean
        @ConditionalOnMapProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "default-headers")
        WebClientCustomizer defaultHeadersWebClientCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j |- Default Headers Web Client Customizer");
            return webClientBuilder -> {
                webClientBuilder.defaultHeaders(header -> properties.getDefaultHeaders().forEach(header::set));
            };
        }

        @Bean
        @ConditionalOnProperty(prefix = WebPropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "logger-enabled", havingValue = "true")
        WebClientCustomizer logHeadersWebClientCustomizer() {
            log.debug("Tutorials4j |- Log Headers Web Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.filter(logResponse());
                restClientBuilder.filter(logRequest());
            };
        }

        @Bean
        WebClientCustomizer defaultWebClientCustomizer() {
            log.debug("Tutorials4j |- default Web Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.filter(catchExcepiton());
            };
        }

        private ExchangeFilterFunction catchExcepiton() {
            return ExchangeFilterFunction.ofResponseProcessor(response -> {
                HttpStatusCode status = response.statusCode();
                if (status.value() > 300) {
                    return response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new WebClientFrameworkException("接口调用异常: " + body))
                            );
                }

                return Mono.just(response);
            });
        }

        private ExchangeFilterFunction logResponse() {
            return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                log.info("响应: {}", clientResponse.statusCode());
                log.info("--- 响应头列表: ---");

                clientResponse.headers().asHttpHeaders()
                        .forEach(this::logHeader);
                return Mono.just(clientResponse);
            });
        }

        private ExchangeFilterFunction logRequest() {
            return (clientRequest, next) -> {
                log.info("请求: {} {}", clientRequest.method(), clientRequest.url());
                log.info("--- 请求头列表: ---");
                clientRequest.headers().forEach(this::logHeader);
                return next.exchange(clientRequest);
            };
        }

        private void logHeader(String name, List<String> values) {
            values.forEach(value -> log.info("{}={}", name, value));
        }
    }
}
