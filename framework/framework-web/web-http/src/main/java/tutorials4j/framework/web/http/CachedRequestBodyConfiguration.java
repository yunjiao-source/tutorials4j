package tutorials4j.framework.web.http;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.web.core.properties.WebHttpProperties;

/**
 * 缓存请求体配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WebHttpProperties.class)
public class CachedRequestBodyConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Cached Request Body Configuration");
    }

    @Bean
    public FilterRegistrationBean<CachedRequestBodyFilter> cachedBodyFilterRegistration(WebHttpProperties properties) {
        WebHttpProperties.CachedRequestBody crb = properties.getCachedRequestBody();
        FilterRegistrationBean<CachedRequestBodyFilter> registration = new FilterRegistrationBean<>();
        CachedRequestBodyFilter filter = new CachedRequestBodyFilter(properties.getCachedRequestBody());
        registration.setFilter(filter);

        if (ObjectUtils.isNotEmpty(crb.getUrlPatterns())) {
            registration.addUrlPatterns(crb.getUrlPatterns());
        }
        if (crb.getOrder() != null ) {
            registration.setOrder(crb.getOrder());
        }
        if (StringUtils.isNotBlank(crb.getName())) {
            registration.setName(crb.getName());
        }
        if (ObjectUtils.isNotEmpty(crb.getDispatcherTypes())) {
            registration.setDispatcherTypes(crb.getDispatcherTypes());
        }

        log.debug("Tutorials4j |- Cached Request Body Filter");
        return registration;
    }
}
