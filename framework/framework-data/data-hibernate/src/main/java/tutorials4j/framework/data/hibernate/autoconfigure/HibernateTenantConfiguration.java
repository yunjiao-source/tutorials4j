package tutorials4j.framework.data.hibernate.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.data.core.properties.DataTenantProperties;
import tutorials4j.framework.data.hibernate.tenant.Dbcp2MultiTenantConnectionProvider;
import tutorials4j.framework.data.hibernate.tenant.DefaultCurrentTenantIdentifierResolver;
import tutorials4j.framework.data.hibernate.tenant.DruidMultiTenantConnectionProvider;
import tutorials4j.framework.data.hibernate.tenant.HikariMultiTenantConnectionProvider;

import javax.sql.DataSource;

/**
 * 基于Hibernate的租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Session.class)
@ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_TENANT,name = "enabled", havingValue = "true")
public class HibernateTenantConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Data |- Hibernate Tenant Configuration");
    }


    /**
     * TODO
     */
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_TENANT, name = "type", havingValue = "TABLE")
    static class TableTenantConfiguration {
        @Bean
        DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
            log.debug("Tutorials4j - Data |- Default Current Tenant Identifier Resolver");
            return new DefaultCurrentTenantIdentifierResolver();
        }
    }

    /**
     * TODO
     */
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_TENANT, name = "type", havingValue = "DATABASE")
    static class DatabaseTenantConfiguration {
        @Bean
        DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
            log.debug("Tutorials4j - Data |- Default Current Tenant Identifier Resolver");
            return new DefaultCurrentTenantIdentifierResolver();
        }

        @Bean
        @ConditionalOnClass(HikariDataSource.class)
        @ConditionalOnSingleCandidate(HikariDataSource.class)
        HikariMultiTenantConnectionProvider hikariMultiTenantConnectionProvider(DataSource dataSource, DataTenantProperties properties) {
            log.debug("Tutorials4j - Data |- Hikari Multi Tenant Connection Provider");
            HikariMultiTenantConnectionProvider bean = new HikariMultiTenantConnectionProvider();
            bean.init(dataSource, properties);
            return bean;
        }

        @Bean
        @ConditionalOnClass(DruidDataSource.class)
        @ConditionalOnSingleCandidate(DruidDataSource.class)
        DruidMultiTenantConnectionProvider druidMultiTenantConnectionProvider(DataSource dataSource, DataTenantProperties properties) {
            log.debug("Tutorials4j - Data |- Druid Multi Tenant Connection Provider");
            DruidMultiTenantConnectionProvider bean = new DruidMultiTenantConnectionProvider();
            bean.init(dataSource, properties);
            return bean;
        }

        @Bean
        @ConditionalOnClass(BasicDataSource.class)
        @ConditionalOnSingleCandidate(BasicDataSource.class)
        Dbcp2MultiTenantConnectionProvider dbcp2MultiTenantConnectionProvider(DataSource dataSource, DataTenantProperties properties) {
            log.debug("Tutorials4j - Data |- Dbcp2 Multi Tenant Connection Provider");
            Dbcp2MultiTenantConnectionProvider bean = new Dbcp2MultiTenantConnectionProvider();
            bean.init(dataSource, properties);
            return bean;
        }
    }
}
