package tutorials4j.framework.data.hibernate.tenant;

import org.apache.commons.dbcp2.BasicDataSource;
import tutorials4j.framework.data.core.tenant.TenantProperties;

/**
 * dbcp2 多租户数据源提供者
 *
 * @author Yun Jiao
 */
public class Dbcp2MultiTenantConnectionProvider extends AbstractMultiTenantConnectionProvider<BasicDataSource>  {
    @Override
    protected BasicDataSource createDataSource(String tenant, TenantProperties.DataSourceProperties properties) {
        BasicDataSource newDataSource = copyDataSource(defaultDataSource);
        newDataSource.setDriverClassName(properties.getDriverClassName());
        newDataSource.setUrl(properties.getUrl());
        newDataSource.setUsername(properties.getUsername());
        newDataSource.setPassword(properties.getPassword());
        return newDataSource;
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
