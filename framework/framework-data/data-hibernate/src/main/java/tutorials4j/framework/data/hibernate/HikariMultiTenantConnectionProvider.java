package tutorials4j.framework.data.hibernate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import tutorials4j.framework.data.core.tenant.TenantProperties;

import java.util.Properties;

/**
 * Hikari 多租户数据源提供者
 *
 * @author Yun Jiao
 */
@Slf4j
public class HikariMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<HikariDataSource> {
    @Override
    protected HikariDataSource createDataSource(TenantProperties.DataSourceProperties dataSourceProperties) {
        Properties defaultDataSourceProperties = defaultDataSource.getDataSourceProperties();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dataSourceProperties.getDriverClassName());
        hikariConfig.setJdbcUrl(dataSourceProperties.getUrl());
        hikariConfig.setUsername(dataSourceProperties.getUsername());
        hikariConfig.setPassword(dataSourceProperties.getPassword());

        if (ObjectUtils.isNotEmpty(defaultDataSource)) {
            defaultDataSourceProperties.forEach((key, value) -> hikariConfig.addDataSourceProperty(String.valueOf(key), value));
        }

        return new HikariDataSource(hikariConfig);
    }
}
