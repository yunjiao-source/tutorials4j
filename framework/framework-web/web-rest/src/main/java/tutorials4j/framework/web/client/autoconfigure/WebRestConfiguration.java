package tutorials4j.framework.web.client.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequest;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.web.client.interceptor.LogClientHttpRequestInterceptor;
import tutorials4j.framework.web.client.util.ReactiveClientUtils;
import tutorials4j.framework.web.core.properties.WebClientProperties;

/**
 * web client 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebRestConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Web Rest Configuration");
    }

    @Configuration(proxyBeanMethods = false)
    public static class RestClientConfiguration {
        @Bean
        RestTemplateRequestCustomizer<ClientHttpRequest> defaultHeadersRestTemplateRequestCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j - Web |- Default Headers Rest Template Request Customizer");
            return request -> {
                properties.getDefaultHeaders().forEach(request.getHeaders()::set);
            };
        }

        @Bean
        @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "logger", havingValue = "true")
        RestTemplateCustomizer logHeadersRestTemplateCustomizer() {
            log.debug("Tutorials4j - Web |- Log Headers Rest Template Builder Customizer");
            return restTemplate -> {
                restTemplate.getInterceptors().add(new LogClientHttpRequestInterceptor());
            };
        }

        @Bean
        @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "logger", havingValue = "true")
        RestClientCustomizer logHeadersRestClientCustomizer() {
            log.debug("Tutorials4j - Web |- Log Headers Rest Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.requestInterceptor(new LogClientHttpRequestInterceptor());
            };
        }

        @Bean
        RestClientCustomizer defaultHeadersRestClientCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j - Web |- Default Headers Rest Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.defaultHeaders(header -> properties.getDefaultHeaders().forEach(header::set));
            };
        }

        @Bean
        WebClientCustomizer defaultHeadersWebClientCustomizer(WebClientProperties properties) {
            log.debug("Tutorials4j - Web |- Default Headers Web Client Customizer");
            return webClientBuilder -> {
                webClientBuilder.defaultHeaders(header -> properties.getDefaultHeaders().forEach(header::set));
            };
        }

        @Bean
        @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT,name = "logger", havingValue = "true")
        WebClientCustomizer logHeadersWebClientCustomizer() {
            log.debug("Tutorials4j - Web |- Log Headers Web Client Customizer");
            return webClientBuilder -> {
                webClientBuilder.filter(ReactiveClientUtils.ofClientRequestLogger());
                webClientBuilder.filter(ReactiveClientUtils.ofClientResponseLogger());
            };
        }

        @Bean
        WebClientCustomizer defaultWebClientCustomizer() {
            log.debug("Tutorials4j - Web |- Default Web Client Customizer");
            return restClientBuilder -> {
                restClientBuilder.filter(ReactiveClientUtils.ofCatchExcepitonLogger());
            };
        }
    }

}
