package tutorials4j.framework.web.http.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.lang.PropertiesConsts;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.http.CachedRequestBodyFilter;

/**
 * web http 配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebHttpConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Web Http Configuration");
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_HTTP, name = "cached-request-body.enabled", havingValue = "true")
    public static class CachedRequestBodyConfiguration {
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

}
