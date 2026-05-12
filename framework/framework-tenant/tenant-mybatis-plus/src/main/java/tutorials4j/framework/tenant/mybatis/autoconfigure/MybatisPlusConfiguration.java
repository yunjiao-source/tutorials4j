package tutorials4j.framework.tenant.mybatis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.data.mybatis.customizer.MybatisPlusInterceptorCustomizer;
import tutorials4j.framework.tenant.core.properties.TenantProperties;
import tutorials4j.framework.tenant.mybatis.DefaultTenantLineInterceptorCustomizer;

import javax.sql.DataSource;

/**
 * 基于Hibernate的租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class MybatisPlusConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[TENANT-MYBATIS-PLUS] Mybatis Plus Configuration");
    }


    /**
     * 租户配置：共享表
     */
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT, name = "datasource.strategy", havingValue = "table")
    static class TableTenantConfiguration {

        @PostConstruct
        public void postConstruct() {
            log.debug("[TENANT-MYBATIS-PLUS] Table Tenant Configuration");
        }

        @Bean
        MybatisPlusInterceptorCustomizer defaultTenantLineInterceptorCustomizer(TenantProperties properties) {
            log.debug("[TENANT-MYBATIS-PLUS] Default Tenant Line Interceptor Customizer");
            return new DefaultTenantLineInterceptorCustomizer(properties);
        }
    }


}
