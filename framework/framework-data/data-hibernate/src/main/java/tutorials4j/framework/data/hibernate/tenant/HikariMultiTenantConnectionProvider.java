package tutorials4j.framework.data.hibernate.tenant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.data.core.DataFrameworkException;
import tutorials4j.framework.data.core.properties.DataTenantProperties;

import javax.sql.DataSource;

/**
 * Hikari 多租户数据源提供者
 *
 * @author Yun Jiao
 */
@Slf4j
public class HikariMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<HikariDataSource> {
    private HikariDataSource defaultDataSource;

    @Override
    protected HikariDataSource createDataSource(String tenant, DataTenantProperties.DataSourceOptions options) {
        final HikariConfig hikariConfig = new HikariConfig();
        defaultDataSource.copyStateTo(hikariConfig);
        hikariConfig.setDriverClassName(options.getDriverClassName());
        hikariConfig.setJdbcUrl(options.getUrl());
        hikariConfig.setUsername(options.getUsername());
        hikariConfig.setPassword(options.getPassword());

        return new HikariDataSource(hikariConfig);
    }

    @Override
    protected HikariDataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    @Override
    protected void setDefaultDataSource(DataSource dataSource) {
        if (dataSource instanceof HikariDataSource sourceDataSource) {
            this.defaultDataSource = sourceDataSource;
        } else {
            throw new DataFrameworkException("数据源类型不匹配，需要的类型：" +
                    HikariDataSource.class.getSimpleName() + ", 提供的类型：" + dataSource.getClass().getSimpleName());
        }
    }
}
