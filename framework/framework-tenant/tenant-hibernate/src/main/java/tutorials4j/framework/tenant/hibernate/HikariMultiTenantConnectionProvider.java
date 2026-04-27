package tutorials4j.framework.tenant.hibernate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import tutorials4j.framework.tenant.core.exception.DataSourceTypeMismatch;
import tutorials4j.framework.tenant.core.properties.TenantDataSourceProperties;

import javax.sql.DataSource;

/**
 * 基于 HikariCP 连接池的多租户连接提供者实现。
 * <p>
 * 利用 HikariCP 的 {@link HikariConfig} 复制默认数据源的配置，
 * 为每个租户创建独立的 {@link HikariDataSource} 实例。
 * 要求默认数据源必须是 {@link HikariDataSource} 类型。
 * </p>
 *
 * @author Yun Jiao
 * @see HikariDataSource
 * @see HikariConfig
 */
@Slf4j
public class HikariMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<HikariDataSource> {
    private HikariDataSource defaultDataSource;

    @Override
    protected HikariDataSource createDataSource(String tenant, TenantDataSourceProperties.ConnectionOptions options) {
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
            throw new DataSourceTypeMismatch(BasicDataSource.class.getSimpleName(), dataSource.getClass().getSimpleName());
        }
    }
}
