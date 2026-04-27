package tutorials4j.framework.tenant.hibernate.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.tenant.core.properties.TenantDatabaseProperties;
import tutorials4j.framework.tenant.hibernate.Dbcp2MultiTenantConnectionProvider;
import tutorials4j.framework.tenant.hibernate.DefaultCurrentTenantIdentifierResolver;
import tutorials4j.framework.tenant.hibernate.DruidMultiTenantConnectionProvider;
import tutorials4j.framework.tenant.hibernate.HikariMultiTenantConnectionProvider;

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
        log.debug("Tutorials4j - Tenant |- Hibernate Configuration");
    }


    /**
     * 租户配置：共享表
     */
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT_DATABASE, name = "strategy", havingValue = "TABLE")
    static class TableTenantConfiguration {
        @Bean
        DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
            log.debug("Tutorials4j - Tenant |- Default Current Tenant Identifier Resolver");
            return new DefaultCurrentTenantIdentifierResolver();
        }
    }

    /**
     * 租户配置：独立数据库
     */
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT_DATABASE, name = "strategy", havingValue = "DATABASE")
    static class DatabaseTenantConfiguration {
        @Bean
        DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
            log.debug("Tutorials4j - Tenant |- Default Current Tenant Identifier Resolver");
            return new DefaultCurrentTenantIdentifierResolver();
        }

        @Bean
        @ConditionalOnClass(HikariDataSource.class)
        @ConditionalOnSingleCandidate(HikariDataSource.class)
        HikariMultiTenantConnectionProvider hikariMultiTenantConnectionProvider(DataSource dataSource, TenantDatabaseProperties properties) {
            log.debug("Tutorials4j - Tenant |- Hikari Multi Tenant Connection Provider");
            HikariMultiTenantConnectionProvider bean = new HikariMultiTenantConnectionProvider();
            bean.init(dataSource, properties);
            return bean;
        }

        @Bean
        @ConditionalOnClass(DruidDataSource.class)
        @ConditionalOnSingleCandidate(DruidDataSource.class)
        DruidMultiTenantConnectionProvider druidMultiTenantConnectionProvider(DataSource dataSource, TenantDatabaseProperties properties) {
            log.debug("Tutorials4j - Tenant |- Druid Multi Tenant Connection Provider");
            DruidMultiTenantConnectionProvider bean = new DruidMultiTenantConnectionProvider();
            bean.init(dataSource, properties);
            return bean;
        }

        @Bean
        @ConditionalOnClass(BasicDataSource.class)
        @ConditionalOnSingleCandidate(BasicDataSource.class)
        Dbcp2MultiTenantConnectionProvider dbcp2MultiTenantConnectionProvider(DataSource dataSource, TenantDatabaseProperties properties) {
            log.debug("Tutorials4j - Tenant |- Dbcp2 Multi Tenant Connection Provider");
            Dbcp2MultiTenantConnectionProvider bean = new Dbcp2MultiTenantConnectionProvider();
            bean.init(dataSource, properties);
            return bean;
        }
    }
}
