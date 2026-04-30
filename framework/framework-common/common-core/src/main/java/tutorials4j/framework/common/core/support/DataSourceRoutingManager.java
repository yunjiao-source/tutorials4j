package tutorials4j.framework.common.core.support;

import tutorials4j.framework.common.core.JdbcOptions;

import javax.sql.DataSource;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface DataSourceRoutingManager {
    DataSource determineTargetDataSource(String tenantIdentifier);

    DataSource getDefaultDataSource();

    void addRoutingJdbcOptions(String name, JdbcOptions jdbcOptions);
}
