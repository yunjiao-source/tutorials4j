package tutorials4j.framework.data.hibernate.tenant;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.data.core.DataFrameworkException;
import tutorials4j.framework.data.core.properties.DataTenantProperties;

import java.sql.SQLException;

/**
 * Druid 多租户数据源提供者
 *
 * @author Yun Jiao
 */
@Slf4j
public class DruidMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<DruidDataSource> {
    @Override
    protected DruidDataSource createDataSource(String tenant, DataTenantProperties.DataSourceOptions options) {
        try {
            DruidDataSource newDataSource = copyDataSource(defaultDataSource);
            newDataSource.setDriverClassName(options.getDriverClassName());
            newDataSource.setUrl(options.getUrl());
            newDataSource.setUsername(options.getUsername());
            newDataSource.setPassword(options.getPassword());
            newDataSource.init();
            return newDataSource;
        } catch (Exception e) {
            throw new DataFrameworkException("创建租户数据源异常",e);
        }
    }

    // 创建数据源并复制属性
    private DruidDataSource copyDataSource(DruidDataSource original) throws SQLException {
        if (original == null) {
            return null;
        }

        DruidDataSource dataSource = new DruidDataSource();

        // 连接池容量
        dataSource.setInitialSize(original.getInitialSize());
        dataSource.setMaxActive(original.getMaxActive());
        dataSource.setMinIdle(original.getMinIdle());
        dataSource.setMaxWait(original.getMaxWait());

        // 连接检测
        dataSource.setValidationQuery(original.getValidationQuery());
        dataSource.setTestWhileIdle(original.isTestWhileIdle());
        dataSource.setTestOnBorrow(original.isTestOnBorrow());
        dataSource.setTestOnReturn(original.isTestOnReturn());

        // 连接回收
        dataSource.setTimeBetweenEvictionRunsMillis(original.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(original.getMinEvictableIdleTimeMillis());

        // KeepAlive
        dataSource.setKeepAlive(original.isKeepAlive());
        dataSource.setKeepAliveBetweenTimeMillis(original.getKeepAliveBetweenTimeMillis());

        // PSCache（MySQL 建议关闭，Oracle/DB2/PostgreSQL 建议开启）
        dataSource.setPoolPreparedStatements(original.isPoolPreparedStatements());

        // Filter
        dataSource.setFilters(String.join(",",original.getFilterClassNames()));

        return dataSource;
    }
}
