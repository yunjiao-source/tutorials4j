package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.support.HandlerInterceptorOptions;
import tutorials4j.framework.common.core.support.ServletFilterOptions;
import tutorials4j.framework.web.core.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.core.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.mvc.filter.XssHttpServletFilter;
import tutorials4j.framework.web.mvc.interceptor.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.mvc.interceptor.IdempotentHandlerInterceptor;

/**
 * 安全配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebMvcSecurityConfiguration implements WebMvcConfigurer {
    @Autowired
    private AccessLimitedCacheTemplate accessLimitedCacheTemplate;
    @Autowired
    private IdempotentCacheTemplate idempotentCacheTemplate;
    @Autowired
    private WebHttpProperties properties;

    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Web |- Security Configuration");
    }

    @Bean
    public FilterRegistrationBean<XssHttpServletFilter> xssHttpServletFilterRegistration() {
        ServletFilterOptions options = properties.getXss();
        FilterRegistrationBean<XssHttpServletFilter> registration = new FilterRegistrationBean<>();
        XssHttpServletFilter filter = new XssHttpServletFilter();
        registration.setFilter(filter);
        options.fill(registration);
        if (log.isDebugEnabled()) {
            log.debug("Tutorials4j - Web |- Xss攻击过滤器：{}", options);
        }
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptorOptions accessLimitedOptions = properties.getAccessLimited();
        AccessLimitedHandlerInterceptor accessLimitedHandlerInterceptor = new AccessLimitedHandlerInterceptor(accessLimitedCacheTemplate);
        registry.addInterceptor(accessLimitedHandlerInterceptor)
                .excludePathPatterns(accessLimitedOptions.getExcludePathPatterns())
                .addPathPatterns(accessLimitedOptions.getIncludePathPatterns());
        log.debug("Tutorials4j - Web |- 添加请求拦截器：{}, {}", accessLimitedHandlerInterceptor, accessLimitedOptions);

        HandlerInterceptorOptions idempotentOptions = properties.getIdempotent();
        IdempotentHandlerInterceptor idempotentHandlerInterceptor = new IdempotentHandlerInterceptor(idempotentCacheTemplate);
        registry.addInterceptor(idempotentHandlerInterceptor)
                .excludePathPatterns(idempotentOptions.getExcludePathPatterns())
                .addPathPatterns(idempotentOptions.getIncludePathPatterns());
        log.debug("Tutorials4j - Web |- 添加请求拦截器：{}, {}", idempotentHandlerInterceptor, idempotentOptions);

    }
}
