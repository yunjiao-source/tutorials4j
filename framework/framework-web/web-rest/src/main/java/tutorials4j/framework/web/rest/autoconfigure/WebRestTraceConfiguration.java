package tutorials4j.framework.web.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.core.support.ServletFilterOptions;
import tutorials4j.framework.common.core.task.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.core.task.TaskDecoratorCreator;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.rest.mdc.TraceExchangeFilterFunction;
import tutorials4j.framework.web.rest.mdc.TraceRequestFilter;
import tutorials4j.framework.web.rest.mdc.TraceRestTemplateInterceptor;
import tutorials4j.framework.web.rest.mdc.TraceTaskDecorator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebRestTraceConfiguration {

    @PostConstruct
    public void postConstruct() {
        log.debug("[WEB-REST] Rest Trace Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    CompositeTaskDecorator compositeTaskDecorator(CompositeTaskDecoratorCreator creator) {
        log.debug("[WEB-REST] Composite Task Decorator");
        return creator.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    TaskDecoratorCreator traceTaskDecoratorCreator() {
        log.debug("[WEB-REST] Trace Task Decorator Creator");
        return TraceTaskDecorator::new;
    }

    @Bean
    RestTemplateCustomizer traceRestTemplateCustomizer() {
        log.debug("[WEB-REST] Trace Rest Template Customizer");
        return restTemplate -> {
            restTemplate.getInterceptors().add(new TraceRestTemplateInterceptor());
        };
    }

    @Bean
    RestClientCustomizer traceRestClientCustomizer() {
        log.debug("[WEB-REST] Trace Rest Client Customizer");
        return restClientBuilder -> {
            restClientBuilder.requestInterceptor(new TraceRestTemplateInterceptor());
        };
    }

    @Bean
    WebClientCustomizer traceWebClientCustomizer() {
        log.debug("[WEB-REST] Trace Web Client Customizer");
        return webClientBuilder -> {
            webClientBuilder.filter(new TraceExchangeFilterFunction());
        };
    }

    @Bean
    FilterRegistrationBean<TraceRequestFilter> traceRequestFilterRegistration(WebHttpProperties properties) {
        ServletFilterOptions options = properties.getTrace();
        FilterRegistrationBean<TraceRequestFilter> registration = new FilterRegistrationBean<>();
        TraceRequestFilter filter = new TraceRequestFilter();
        registration.setFilter(filter);
        options.fill(registration);

        if (log.isDebugEnabled()) {
            log.debug("[WEB-REST] 跟踪信息过滤器：{}", options);
        }
        return registration;
    }
}
