package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.web.core.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.core.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.core.properties.ServletFilterOptions;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.mvc.filter.XssHttpServletFilter;
import tutorials4j.framework.web.mvc.interceptor.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.mvc.interceptor.IdempotentHandlerInterceptor;

import java.util.List;

/**
 * 安全配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration implements WebMvcConfigurer {
    @Autowired
    private AccessLimitedCacheTemplate accessLimitedCacheTemplate;
    @Autowired
    private IdempotentCacheTemplate idempotentCacheTemplate;

    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Security Configuration");
    }

    @Bean
    public FilterRegistrationBean<XssHttpServletFilter> xssHttpServletFilterRegistration(WebHttpProperties properties) {
        ServletFilterOptions options = properties.getXss();
        FilterRegistrationBean<XssHttpServletFilter> registration = new FilterRegistrationBean<>();
        XssHttpServletFilter filter = new XssHttpServletFilter();
        registration.setFilter(filter);

        if (ObjectUtils.isNotEmpty(options.getUrlPatterns())) {
            registration.addUrlPatterns(options.getUrlPatterns());
        }
        if (options.getOrder() != null ) {
            registration.setOrder(options.getOrder());
        }
        if (StringUtils.isNotBlank(options.getName())) {
            registration.setName(options.getName());
        }
        if (ObjectUtils.isNotEmpty(options.getDispatcherTypes())) {
            registration.setDispatcherTypes(options.getDispatcherTypes());
        }
        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- Xss攻击过滤器：{}", options);
        }
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AccessLimitedHandlerInterceptor accessLimitedHandlerInterceptor = new AccessLimitedHandlerInterceptor(accessLimitedCacheTemplate);
        IdempotentHandlerInterceptor idempotentHandlerInterceptor = new IdempotentHandlerInterceptor(idempotentCacheTemplate);
        registry.addInterceptor(accessLimitedHandlerInterceptor);
        registry.addInterceptor(idempotentHandlerInterceptor);
        log.debug("Tutorials4j - Web |- 添加请求拦截器：{}", List.of(accessLimitedHandlerInterceptor, idempotentHandlerInterceptor));
    }
}
