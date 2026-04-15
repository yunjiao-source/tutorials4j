package tutorials4j.framework.web.core.servlet;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存请求体配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CachedRequestBodyProperties.class)
public class CachedRequestBodyConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Cached Request Body Configuration");
    }

    @Bean
    public FilterRegistrationBean<CachedRequestBodyFilter> cachedBodyFilterRegistration(CachedRequestBodyProperties properties) {
        FilterRegistrationBean<CachedRequestBodyFilter> registration = new FilterRegistrationBean<>();
        CachedRequestBodyFilter filter = new CachedRequestBodyFilter(properties);
        registration.setFilter(filter);
        registration.addUrlPatterns(properties.getUrlPatterns());
        registration.setOrder(properties.getOrder());
        registration.setName(properties.getName());
        registration.setDispatcherTypes(properties.getDispatcherTypes());

        log.debug("Tutorials4j |- Cached Request Body Filter");
        return registration;
    }
}
