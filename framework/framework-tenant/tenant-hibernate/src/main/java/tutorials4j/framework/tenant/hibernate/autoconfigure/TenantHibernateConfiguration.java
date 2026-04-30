package tutorials4j.framework.tenant.hibernate.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;
import tutorials4j.framework.data.jdbc.DataSourceRoutingManagerCreator;
import tutorials4j.framework.tenant.core.properties.TenantDataSourceProperties;
import tutorials4j.framework.tenant.hibernate.DefaultCurrentTenantIdentifierResolver;
import tutorials4j.framework.tenant.hibernate.TenantDataSourceBasedMultiTenantConnectionProvider;

import javax.sql.DataSource;

/**
 * 基于Hibernate的租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class TenantHibernateConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Tenant |- Tenant Hibernate Configuration");
    }


    /**
     * 租户配置：共享表
     */
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT_DATASOURCE, name = "strategy", havingValue = "hibernate_table")
    static class TableTenantConfiguration {

        @PostConstruct
        public void postConstruct() {
            log.debug("Tutorials4j - Tenant |- Table Tenant Configuration");
        }

        @Bean
        DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
            log.debug("Tutorials4j - Tenant |- Default Current Tenant Identifier Resolver");
            return new DefaultCurrentTenantIdentifierResolver();
        }
    }

    /**
     * 租户配置：独立数据库
     */
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT_DATASOURCE, name = "strategy", havingValue = "hibernate_database")
    static class DatabaseTenantConfiguration {
        @PostConstruct
        public void postConstruct() {
            log.debug("Tutorials4j - Tenant |- Database Tenant Configuration");
        }
        @Bean
        DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
            log.debug("Tutorials4j - Tenant |- Default Current Tenant Identifier Resolver");
            return new DefaultCurrentTenantIdentifierResolver();
        }

        @Bean
        @ConditionalOnMissingBean
        TenantDataSourceBasedMultiTenantConnectionProvider tenantDataSourceBasedMultiTenantConnectionProvider(DataSourceRoutingManagerCreator creator,
                                                                                                              TenantDataSourceProperties properties) {
            log.debug("Tutorials4j - Tenant |- Tenant Data Source Based Multi Tenant Connection Provider");
            DataSourceRoutingManager dataSourceRoutingManager = creator.get();

            properties.getJdbc().forEach((k, v) -> dataSourceRoutingManager.addRoutingJdbcOptions(k.toUpperCase(), v));
            return new TenantDataSourceBasedMultiTenantConnectionProvider(dataSourceRoutingManager);
        }

    }
}
