package tutorials4j.framework.web.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequest;
import tutorials4j.framework.web.core.properties.WebProperties;
import tutorials4j.framework.web.rest.util.RestUtils;

/**
 * Rest 客户端默认配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebRestClientDefaultConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Rest Client Default Configuration");
    }

    @Bean
    RestTemplateRequestCustomizer<ClientHttpRequest> defaultHeadersRestTemplateRequestCustomizer(WebProperties properties) {
        log.debug("Tutorials4j - Web |- Default Headers Rest Template Request Customizer");
        return request -> {
            properties.getClient().getDefaultHeaders().forEach(request.getHeaders()::set);
        };
    }

    @Bean
    RestClientCustomizer defaultHeadersRestClientCustomizer(WebProperties properties) {
        log.debug("Tutorials4j - Web |- Default Headers Rest Client Customizer");
        return restClientBuilder -> {
            restClientBuilder.defaultHeaders(header -> properties.getClient().getDefaultHeaders().forEach(header::set));
        };
    }

    @Bean
    WebClientCustomizer defaultHeadersWebClientCustomizer(WebProperties properties) {
        log.debug("Tutorials4j - Web |- Default Headers Web Client Customizer");
        return webClientBuilder -> {
            webClientBuilder.defaultHeaders(header -> properties.getClient().getDefaultHeaders().forEach(header::set));
        };
    }

    @Bean
    WebClientCustomizer defaultWebClientCustomizer() {
        log.debug("Tutorials4j - Web |- Default Web Client Customizer");
        return restClientBuilder -> {
            restClientBuilder.filter(RestUtils.ofCatchExcepitonLogger());
        };
    }
}
