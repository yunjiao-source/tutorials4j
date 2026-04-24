package tutorials4j.framework.data.hibernate.tenant;

import org.apache.commons.dbcp2.BasicDataSource;
import tutorials4j.framework.data.core.DataFrameworkException;
import tutorials4j.framework.data.core.properties.DataTenantProperties;

import javax.sql.DataSource;

/**
 * dbcp2 多租户数据源提供者
 *
 * @author Yun Jiao
 */
public class Dbcp2MultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<BasicDataSource>  {
    private BasicDataSource defaultDataSource;

    @Override
    protected BasicDataSource createDataSource(String tenant, DataTenantProperties.DataSourceOptions options) {
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
            throw new DataFrameworkException("数据源类型不匹配，需要的类型：" +
                    BasicDataSource.class.getSimpleName() + ", 提供的类型：" + dataSource.getClass().getSimpleName());
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
