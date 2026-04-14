package tutorials4j.springboot3;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置多数据源
 *
 * @author Yun Jiao
 */
@Configuration(proxyBeanMethods = false)
@EnableTransactionManagement
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("tenants")
    public Map<String, Map<String, String>> tenantDataSourceProperties() {
        return new HashMap<>();
    }

    @Bean
    public DataSource routingDataSource(Map<String, Map<String, String>> tenantDataSourceProperties) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        tenantDataSourceProperties.forEach((tenantId, props) -> {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(props.get("url"));
            ds.setUsername(props.get("username"));
            ds.setPassword(props.get("password"));
            ds.setDriverClassName(props.get("driver-class-name"));
            targetDataSources.put(tenantId, ds);
        });

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(targetDataSources.get("tenant1")); // 默认
        return routingDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
