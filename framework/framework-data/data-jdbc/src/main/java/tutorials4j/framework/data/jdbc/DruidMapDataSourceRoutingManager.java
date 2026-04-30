package tutorials4j.framework.data.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.common.core.exception.FrameworkRuntimeException;
import tutorials4j.framework.data.core.exception.DataSourceTypeMismatchException;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class DruidMapDataSourceRoutingManager extends AbstractMapDataSourceRoutingManager {
    @Override
    protected DataSource createDataSource(String tenant, JdbcOptions options) {
        DataSource defaultDataSource = getDefaultDataSource();

        if (defaultDataSource instanceof DruidDataSource druidDataSource) {
            try {
                DruidDataSource newDataSource = copyDataSource(druidDataSource);
                newDataSource.setDriverClassName(options.getDriverClassName());
                newDataSource.setUrl(options.getUrl());
                newDataSource.setUsername(options.getUsername());
                newDataSource.setPassword(options.getPassword());
                newDataSource.init();
                return newDataSource;
            } catch (Exception e) {
                throw new FrameworkRuntimeException("创建数据源异常",e);
            }
        } else {
            throw new DataSourceTypeMismatchException(DruidDataSource.class.getSimpleName(), defaultDataSource.getClass().getSimpleName());
        }
    }
    /**
     * 复制 Druid 数据源的连接池配置。
     *
     * @param original 原始默认数据源
     * @return 配置属性相同但尚未初始化的新数据源
     * @throws SQLException 如果获取原数据源配置时发生异常
     */
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
