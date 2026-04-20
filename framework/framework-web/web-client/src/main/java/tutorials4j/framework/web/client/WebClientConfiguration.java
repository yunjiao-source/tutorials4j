package tutorials4j.framework.web.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import tutorials4j.framework.common.core.condition.ConditionalOnMapProperty;
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

}
