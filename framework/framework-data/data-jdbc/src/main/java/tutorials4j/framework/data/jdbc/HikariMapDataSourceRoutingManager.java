package tutorials4j.framework.data.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.data.core.exception.DataSourceTypeMismatchException;

import javax.sql.DataSource;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class HikariMapDataSourceRoutingManager extends AbstractMapDataSourceRoutingManager {
    @Override
    protected DataSource createDataSource(String tenant, JdbcOptions options) {
        DataSource defaultDataSource = getDefaultDataSource();

        if (defaultDataSource instanceof HikariDataSource hikariDataSource) {
            final HikariConfig hikariConfig = new HikariConfig();
            hikariDataSource.copyStateTo(hikariConfig);
            hikariConfig.setDriverClassName(options.getDriverClassName());
            hikariConfig.setJdbcUrl(options.getUrl());
            hikariConfig.setUsername(options.getUsername());
            hikariConfig.setPassword(options.getPassword());

            return new HikariDataSource(hikariConfig);
        } else {
            throw new DataSourceTypeMismatchException(BasicDataSource.class.getSimpleName(), defaultDataSource.getClass().getSimpleName());
        }
    }
}
