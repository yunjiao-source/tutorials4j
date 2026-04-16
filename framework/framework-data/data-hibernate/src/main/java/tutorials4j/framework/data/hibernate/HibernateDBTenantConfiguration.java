package tutorials4j.framework.data.hibernate;

import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.data.core.tenant.TenantProperties;

import javax.sql.DataSource;

/**
 * 独立数据库租户配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HibernateDBTenantConfiguration {

    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Hibernate DB Tenant Configuration");
    }

    @Bean
    DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
        log.debug("Tutorials4j |- Default Current Tenant Identifier Resolver");
        return new DefaultCurrentTenantIdentifierResolver();
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnClass(HikariDataSource.class)
    HikariMultiTenantConnectionProvider hikariMultiTenantConnectionProvider(DataSource dataSource, TenantProperties properties) {
        log.debug("Tutorials4j |- Hikari Multi Tenant Connection Provider");
        HikariMultiTenantConnectionProvider bean = new HikariMultiTenantConnectionProvider();
        bean.init(dataSource, properties);
        return bean;
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnClass(DruidDataSource.class)
    DruidMultiTenantConnectionProvider druidMultiTenantConnectionProvider(DataSource dataSource, TenantProperties properties) {
        log.debug("Tutorials4j |- Druid Multi Tenant Connection Provider");
        DruidMultiTenantConnectionProvider bean = new DruidMultiTenantConnectionProvider();
        bean.init(dataSource, properties);
        return bean;
    }
}
