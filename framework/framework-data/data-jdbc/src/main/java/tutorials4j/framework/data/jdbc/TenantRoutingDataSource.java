package tutorials4j.framework.data.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.AbstractDataSource;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.sql.SQLException;
import java.sql.ShardingKeyBuilder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantRoutingDataSource extends AbstractDataSource {
    private final DataSourceRoutingManager dataSourceRoutingManager;

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    @Override
    public ConnectionBuilder createConnectionBuilder() throws SQLException {
        return determineTargetDataSource().createConnectionBuilder();
    }

    @Override
    public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
        return determineTargetDataSource().createShardingKeyBuilder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
    }

    protected DataSource determineTargetDataSource() {
        return dataSourceRoutingManager.determineTargetDataSource(TenantContextHolder.get());
    }



}
