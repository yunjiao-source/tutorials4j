package tutorials4j.framework.web.rest.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import tutorials4j.framework.common.core.task.CompositeTaskDecoratorCreator;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;
import tutorials4j.framework.web.core.properties.ServletFilterOptions;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.rest.mdc.TraceExchangeFilterFunction;
import tutorials4j.framework.web.rest.mdc.TraceFilter;
import tutorials4j.framework.web.rest.mdc.TraceRestTemplateInterceptor;
import tutorials4j.framework.web.rest.mdc.TraceTaskDecorator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class TraceConfiguration {

    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Rest Trace Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    CompositeTaskDecorator compositeTaskDecorator(CompositeTaskDecoratorCreator creator) {
        log.debug("Tutorials4j - Web |- Composite Task Decorator");
        return creator.get();
    }

    @Bean
    TaskDecoratorSupplier traceTaskDecoratorSupplier() {
        log.debug("Tutorials4j - Web |- Trace Task Decorator");
        return TraceTaskDecorator::new;
    }

    @Bean
    RestTemplateCustomizer traceRestTemplateCustomizer() {
        log.debug("Tutorials4j - Web |- Trace Rest Template Customizer");
        return restTemplate -> {
            restTemplate.getInterceptors().add(new TraceRestTemplateInterceptor());
        };
    }

    @Bean
    RestClientCustomizer traceRestClientCustomizer() {
        log.debug("Tutorials4j - Web |- Trace Rest Client Customizer");
        return restClientBuilder -> {
            restClientBuilder.requestInterceptor(new TraceRestTemplateInterceptor());
        };
    }

    @Bean
    WebClientCustomizer traceWebClientCustomizer() {
        log.debug("Tutorials4j - Web |- Trace Web Client Customizer");
        return webClientBuilder -> {
            webClientBuilder.filter(new TraceExchangeFilterFunction());
        };
    }

    @Bean
    FilterRegistrationBean<TraceFilter> traceFilterRegistration(WebHttpProperties properties) {
        ServletFilterOptions options = properties.getTrace();
        FilterRegistrationBean<TraceFilter> registration = new FilterRegistrationBean<>();
        TraceFilter filter = new TraceFilter();
        registration.setFilter(filter);

        if (ObjectUtils.isNotEmpty(options.getUrlPatterns())) {
            registration.addUrlPatterns(options.getUrlPatterns());
        }
        if (StringUtils.isNotBlank(options.getName())) {
            registration.setName(options.getName());
        }
        if (options.getOrder() != null ) {
            registration.setOrder(options.getOrder());
        }
        if (ObjectUtils.isNotEmpty(options.getDispatcherTypes())) {
            registration.setDispatcherTypes(options.getDispatcherTypes());
        }
        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- 跟踪信息过滤器：{}", options);
        }
        return registration;
    }
}
