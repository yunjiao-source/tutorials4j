package tutorials4j.framework.data.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;
import tutorials4j.framework.data.core.properties.DataTenantProperties;
import tutorials4j.framework.data.core.tenant.TenantHandlerInterceptor;
import tutorials4j.framework.data.core.tenant.TenantTaskDecorator;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class DataTenantConfiguration implements WebMvcConfigurer {
    private final DataTenantProperties properties;

    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Data Tenant Configuration");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantHandlerInterceptor()).addPathPatterns(properties.getPathPatterns());
    }

    @Bean
    @ConditionalOnBean(CompositeTaskDecorator.class)
    TaskDecoratorSupplier tenantTaskDecoratorSupplier() {
        log.debug("Tutorials4j |- Tenant Task Decorator Supplier");
        return TenantTaskDecorator::new;
    }
}
