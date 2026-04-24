package tutorials4j.framework.data.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.CompositeTaskDecorator;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.task.TaskDecoratorSupplier;
import tutorials4j.framework.data.core.properties.DataTenantProperties;
import tutorials4j.framework.data.core.tenant.TenantHandlerInterceptor;
import tutorials4j.framework.data.core.tenant.TenantTaskDecorator;

import java.util.Arrays;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_TENANT,name = "enabled", havingValue = "true")
public class DataTenantConfiguration implements WebMvcConfigurer {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Data |- Data Tenant Configuration");
    }


    @Bean
    @ConditionalOnBean(CompositeTaskDecorator.class)
    TaskDecoratorSupplier tenantTaskDecoratorSupplier() {
        log.debug("Tutorials4j - Data |- Tenant Task Decorator Supplier");
        return TenantTaskDecorator::new;
    }

    @Configuration
    @RequiredArgsConstructor
    public static class TenantWebMvcConfigurer implements WebMvcConfigurer {
        private final DataTenantProperties properties;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            TenantHandlerInterceptor interceptor = new TenantHandlerInterceptor();
            log.debug("Tutorials4j - Data |- 租户拦截器[{}]注册到InterceptorRegistry, 有效路径：{}", interceptor,
                    Arrays.toString(properties.getPathPatterns()));
            registry.addInterceptor(interceptor).addPathPatterns(properties.getPathPatterns());
        }
    }
}
