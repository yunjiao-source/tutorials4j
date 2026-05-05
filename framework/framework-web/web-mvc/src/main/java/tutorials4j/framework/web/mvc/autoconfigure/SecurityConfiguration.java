package tutorials4j.framework.web.mvc.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.web.core.cache.AccessLimitedCacheTemplate;
import tutorials4j.framework.web.core.cache.IdempotentCacheTemplate;
import tutorials4j.framework.web.mvc.security.AccessLimitedHandlerInterceptor;
import tutorials4j.framework.web.mvc.security.IdempotentHandlerInterceptor;

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


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AccessLimitedHandlerInterceptor accessLimitedHandlerInterceptor = new AccessLimitedHandlerInterceptor(accessLimitedCacheTemplate);
        IdempotentHandlerInterceptor idempotentHandlerInterceptor = new IdempotentHandlerInterceptor(idempotentCacheTemplate);
        registry.addInterceptor(accessLimitedHandlerInterceptor);
        registry.addInterceptor(idempotentHandlerInterceptor);
        log.debug("Tutorials4j - Web |- 添加请求拦截器：{}", List.of(accessLimitedHandlerInterceptor, idempotentHandlerInterceptor));
    }
}
