package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.support.ServletFilterOptions;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.mvc.filter.CachedBodyRequestFilter;

/**
 * 缓存请求体配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebMvcCachedBodyConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Cached Body Configuration");
    }


    @Bean
    FilterRegistrationBean<CachedBodyRequestFilter> cachedBodyFilterRegistration(WebHttpProperties properties) {
        ServletFilterOptions options = properties.getCachedBody();
        FilterRegistrationBean<CachedBodyRequestFilter> registration = new FilterRegistrationBean<>();
        CachedBodyRequestFilter filter = new CachedBodyRequestFilter();
        registration.setFilter(filter);
        options.fill(registration);

        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- 缓存请求体过滤器：{}", options);
        }
        return registration;
    }
}
