package tutorials4j.framework.tenant.hibernate;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import tutorials4j.framework.tenant.core.exception.DataSourceTypeMismatch;
import tutorials4j.framework.tenant.core.exception.TenantFrameworkException;
import tutorials4j.framework.tenant.core.properties.TenantDatabaseProperties;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 基于阿里巴巴 Druid 连接池的多租户连接提供者实现。
 * <p>
 * 为每个租户动态创建独立的 Druid 数据源实例。
 * 新建数据源会复制默认数据源的大部分连接池参数（容量、检测、回收策略等），
 * 然后覆盖其驱动类名、URL、用户名和密码。
 * 该实现依赖 Druid 特有的配置属性，要求默认数据源必须是 {@link DruidDataSource} 类型。
 * </p>
 *
 * @author Yun Jiao
 * @see DruidDataSource
 */
@Slf4j
public class DruidMultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<DruidDataSource> {
    private DruidDataSource defaultDataSource;

    @Override
    protected DruidDataSource createDataSource(String tenant, TenantDatabaseProperties.DataSourceOptions options) {
        try {
            DruidDataSource newDataSource = copyDataSource(defaultDataSource);
            newDataSource.setDriverClassName(options.getDriverClassName());
            newDataSource.setUrl(options.getUrl());
            newDataSource.setUsername(options.getUsername());
            newDataSource.setPassword(options.getPassword());
            newDataSource.init();
            return newDataSource;
        } catch (Exception e) {
            throw new TenantFrameworkException("创建租户数据源异常",e);
        }
    }

    @Override
    protected DruidDataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    @Override
    protected void setDefaultDataSource(DataSource dataSource) {
        if (dataSource instanceof DruidDataSource sourceDataSource) {
            this.defaultDataSource = sourceDataSource;
        } else {
            throw new DataSourceTypeMismatch(BasicDataSource.class.getSimpleName(), dataSource.getClass().getSimpleName());
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
