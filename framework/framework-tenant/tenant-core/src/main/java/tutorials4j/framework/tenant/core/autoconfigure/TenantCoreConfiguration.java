package tutorials4j.framework.tenant.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;
import tutorials4j.framework.tenant.core.TenantHandlerInterceptor;
import tutorials4j.framework.tenant.core.TenantTaskDecorator;
import tutorials4j.framework.tenant.core.properties.TenantCacheProperties;
import tutorials4j.framework.tenant.core.properties.TenantDatabaseProperties;
import tutorials4j.framework.tenant.core.properties.TenantProperties;

import java.util.Arrays;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@EnableConfigurationProperties({TenantProperties.class
        , TenantCacheProperties.class
        , TenantDatabaseProperties.class})
public class TenantCoreConfiguration implements WebMvcConfigurer {
    private final TenantProperties properties;

    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Tenant |- Tenant Configuration");
    }


    @Bean
    @ConditionalOnBean(CompositeTaskDecorator.class)
    TaskDecoratorSupplier tenantTaskDecoratorSupplier() {
        log.debug("Tutorials4j - Tenant |- Tenant Task Decorator Supplier");
        return TenantTaskDecorator::new;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        TenantHandlerInterceptor interceptor = new TenantHandlerInterceptor();
        log.debug("Tutorials4j - Tenant |- 租户拦截器[TenantHandlerInterceptor]注册到InterceptorRegistry, 有效路径：{}",
                Arrays.toString(properties.getPathPatterns()));
        registry.addInterceptor(interceptor).addPathPatterns(properties.getPathPatterns());
    }

}
