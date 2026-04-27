package tutorials4j.framework.tenant.hibernate;

import org.apache.commons.dbcp2.BasicDataSource;
import tutorials4j.framework.tenant.core.exception.DataSourceTypeMismatch;
import tutorials4j.framework.tenant.core.properties.TenantDataSourceProperties;

import javax.sql.DataSource;

/**
 * 基于 Apache Commons DBCP2 连接池的多租户连接提供者实现。
 * <p>
 * 为每个租户动态创建独立的 {@link BasicDataSource} 实例。
 * 新建数据源会复制默认数据源的连接池核心参数（初始大小、最大连接数、空闲数、等待时间等），
 * 以及连接校验、事务隔离等配置，然后覆盖驱动类名、URL、用户名和密码。
 * 要求默认数据源必须是 {@link BasicDataSource} 类型。
 * </p>
 *
 * @author Yun Jiao
 * @see BasicDataSource
 */
public class Dbcp2MultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<BasicDataSource>  {
    private BasicDataSource defaultDataSource;

    @Override
    protected BasicDataSource createDataSource(String tenant, TenantDataSourceProperties.ConnectionOptions options) {
        BasicDataSource newDataSource = copyDataSource(defaultDataSource);
        newDataSource.setDriverClassName(options.getDriverClassName());
        newDataSource.setUrl(options.getUrl());
        newDataSource.setUsername(options.getUsername());
        newDataSource.setPassword(options.getPassword());
        return newDataSource;
    }

    @Override
    protected BasicDataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    @Override
    protected void setDefaultDataSource(DataSource dataSource) {
        if (dataSource instanceof BasicDataSource sourceDataSource) {
            this.defaultDataSource = sourceDataSource;
        } else {
            throw new DataSourceTypeMismatch(BasicDataSource.class.getSimpleName(), dataSource.getClass().getSimpleName());
        }
    }

    // 创建数据源并复制属性
    private BasicDataSource copyDataSource(BasicDataSource original) {
        if (original == null) {
            return null;
        }

        BasicDataSource copy = new BasicDataSource();

        // ========== 2. 连接池核心参数 ==========
        copy.setInitialSize(original.getInitialSize());        // 初始连接数
        copy.setMaxTotal(original.getMaxTotal());            // 最大活跃连接数
        copy.setMaxIdle(original.getMaxIdle());                // 最大空闲连接数
        copy.setMinIdle(original.getMinIdle());                // 最小空闲连接数
        copy.setMaxWait(original.getMaxWaitDuration());                // 获取连接最大等待时间

        // ========== 3. 连接校验与超时配置 ==========
        copy.setValidationQuery(original.getValidationQuery());// 连接校验SQL
        copy.setTestOnBorrow(original.getTestOnBorrow());       // 取出连接时校验
        copy.setTestOnReturn(original.getTestOnReturn());       // 归还连接时校验
        copy.setTestWhileIdle(original.getTestWhileIdle());     // 空闲时校验
        copy.setDurationBetweenEvictionRuns(original.getDurationBetweenEvictionRuns());
        copy.setMinEvictableIdle(original.getMinEvictableIdleDuration());

        // ========== 4. 事务与其他高级配置 ==========
        copy.setDefaultAutoCommit(original.getDefaultAutoCommit());
        copy.setDefaultReadOnly(original.getDefaultReadOnly());
        copy.setDefaultTransactionIsolation(original.getDefaultTransactionIsolation());

        return copy;
    }
}
