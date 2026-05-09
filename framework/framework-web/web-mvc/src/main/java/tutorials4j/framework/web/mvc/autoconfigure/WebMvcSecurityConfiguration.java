package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.json.JsonConsts;
import tutorials4j.framework.common.core.support.HandlerInterceptorOptions;
import tutorials4j.framework.common.core.support.ServletFilterOptions;
import tutorials4j.framework.web.core.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.core.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.core.properties.WebHttpProperties;
import tutorials4j.framework.web.mvc.filter.XssHttpServletFilter;
import tutorials4j.framework.web.mvc.interceptor.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.mvc.interceptor.IdempotentHandlerInterceptor;
import tutorials4j.framework.web.mvc.support.XssSimpleModule;

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
        log.debug("Tutorials4j - Web |- Web Mvc Security Configuration");
    }

    @Bean
    @Order(JsonConsts.MODULE_ORDER_XSS)
    XssSimpleModule xssSimpleModule() {
        log.debug("Tutorials4j - Web |- Xss Simple Module");
        return new XssSimpleModule();
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
        doAddInterceptors(registry, accessLimitedHandlerInterceptor, accessLimitedOptions);

        HandlerInterceptorOptions idempotentOptions = properties.getIdempotent();
        IdempotentHandlerInterceptor idempotentHandlerInterceptor = new IdempotentHandlerInterceptor(idempotentCacheTemplate);
        doAddInterceptors(registry, idempotentHandlerInterceptor, idempotentOptions);

    }

    private void doAddInterceptors(InterceptorRegistry registry, HandlerInterceptor interceptor, HandlerInterceptorOptions options) {
        if (options.getExcludePathPatterns().length == 0 && options.getIncludePathPatterns().length == 0) {
            log.warn("请求拦截器'{}' 未配置，原因是'include-path-patterns'或‘exclude-path-patterns’没有设置值", interceptor);
            return;
        }

        InterceptorRegistration registration = registry.addInterceptor(interceptor);
        if (options.getExcludePathPatterns().length > 0) {
            registration.excludePathPatterns(options.getExcludePathPatterns());
        }

        if (options.getIncludePathPatterns().length > 0) {
            registration.addPathPatterns(options.getIncludePathPatterns());
        }

        log.debug("Tutorials4j - Web |- 添加请求拦截器：{}, {}", interceptor, options);
    }
}
