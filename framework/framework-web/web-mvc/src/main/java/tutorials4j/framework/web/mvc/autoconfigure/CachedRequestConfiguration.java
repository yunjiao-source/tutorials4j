package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.mvc.support.CachedRequestFilter;

/**
 * http 缓存请求体配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CachedRequestConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Cached Request Body Configuration");
    }


    @Bean
    public FilterRegistrationBean<CachedRequestFilter> cachedBodyFilterRegistration(WebHttpProperties properties) {
        WebHttpProperties.CachedRequest cachedRequest = properties.getCachedRequest();
        FilterRegistrationBean<CachedRequestFilter> registration = new FilterRegistrationBean<>();
        CachedRequestFilter filter = new CachedRequestFilter(properties.getCachedRequest());
        registration.setFilter(filter);

        if (ObjectUtils.isNotEmpty(cachedRequest.getUrlPatterns())) {
            registration.addUrlPatterns(cachedRequest.getUrlPatterns());
        }
        if (cachedRequest.getOrder() != null ) {
            registration.setOrder(cachedRequest.getOrder());
        }
        if (StringUtils.isNotBlank(cachedRequest.getName())) {
            registration.setName(cachedRequest.getName());
        }
        if (ObjectUtils.isNotEmpty(cachedRequest.getDispatcherTypes())) {
            registration.setDispatcherTypes(cachedRequest.getDispatcherTypes());
        }
        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- 缓存请求体过滤器：{}", cachedRequest);
        }
        return registration;
    }
}
