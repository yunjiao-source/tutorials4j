package tutorials4j.framework.web.http.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
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
        log.debug("Tutorials4j - Web |- Web Http Configuration");
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_HTTP_CACHED_REQUEST_BODY, name = "enabled", havingValue = "true")
    public static class CachedRequestBodyConfiguration {

        @PostConstruct
        public void postConstruct() {
            log.debug("Tutorials4j - Web |- Cached Request Body Configuration");
        }


        @Bean
        public FilterRegistrationBean<CachedRequestBodyFilter> cachedBodyFilterRegistration(WebHttpProperties properties) {
            WebHttpProperties.CachedRequestBody cachedRequestBody = properties.getCachedRequestBody();
            FilterRegistrationBean<CachedRequestBodyFilter> registration = new FilterRegistrationBean<>();
            CachedRequestBodyFilter filter = new CachedRequestBodyFilter(properties.getCachedRequestBody());
            registration.setFilter(filter);

            if (ObjectUtils.isNotEmpty(cachedRequestBody.getUrlPatterns())) {
                registration.addUrlPatterns(cachedRequestBody.getUrlPatterns());
            }
            if (cachedRequestBody.getOrder() != null ) {
                registration.setOrder(cachedRequestBody.getOrder());
            }
            if (StringUtils.isNotBlank(cachedRequestBody.getName())) {
                registration.setName(cachedRequestBody.getName());
            }
            if (ObjectUtils.isNotEmpty(cachedRequestBody.getDispatcherTypes())) {
                registration.setDispatcherTypes(cachedRequestBody.getDispatcherTypes());
            }

            log.debug("Tutorials4j Web |- 缓存请求体过滤器[CachedRequestBodyFilter]成功注册，配置信息：{}", cachedRequestBody);
            return registration;
        }
    }

}
