package tutorials4j.framework.autoconfigure.servlet;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.core.servlet.CachedBodyFilter;

/**
 * 缓存请求体配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CachedBodyProperties.class)
public class CachedBodyConfig {
    @PostConstruct
    public void postConstruct() {
        log.debug("Turorials4j |- Cached Body Filter Config");
    }

    @Bean
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilterRegistration(CachedBodyProperties properties) {
        FilterRegistrationBean<CachedBodyFilter> registration = new FilterRegistrationBean<>();
        CachedBodyFilter filter = new CachedBodyFilter();
        if (properties.getMaxContentLength() != null) {
            filter.setMaxContentLength(properties.getMaxContentLength().toBytes());
        }
        registration.setFilter(filter);
        registration.addUrlPatterns(properties.getUrlPatterns());
        registration.setOrder(properties.getOrder());
        registration.setName(properties.getName());
        registration.setDispatcherTypes(properties.getDispatcherTypes());

        log.debug("Turorials4j |- Filter Registration Bean - {}", registration);
        return registration;
    }
}
