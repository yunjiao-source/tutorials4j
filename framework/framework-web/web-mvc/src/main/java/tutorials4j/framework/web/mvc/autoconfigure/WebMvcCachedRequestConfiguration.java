package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.mvc.filter.CachedRequestFilter;

/**
 * 缓存请求体配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebMvcCachedRequestConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Cached Request Body Configuration");
    }


    @Bean
    public FilterRegistrationBean<CachedRequestFilter> cachedBodyFilterRegistration(WebHttpProperties properties) {
        WebHttpProperties.CachedRequestOptions options = properties.getCachedRequest();
        FilterRegistrationBean<CachedRequestFilter> registration = new FilterRegistrationBean<>();
        CachedRequestFilter filter = new CachedRequestFilter(properties.getCachedRequest());
        registration.setFilter(filter);
        options.fill(registration);

        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- 缓存请求体过滤器：{}", options);
        }
        return registration;
    }
}
