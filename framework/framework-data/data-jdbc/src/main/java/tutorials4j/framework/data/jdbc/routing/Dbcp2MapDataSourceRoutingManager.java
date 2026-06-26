package tutorials4j.framework.data.jdbc.routing;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.data.core.exception.DataErrorCode;

/**
 * 基于 Apache DBCP2 连接池的数据源路由管理器实现。
 *
 * <p>当默认数据源为 {@link BasicDataSource} 时，通过复制其配置并为每个租户创建独立的 {@code BasicDataSource} 实例。
 * 复制的属性包括连接池核心参数、连接校验与超时、事务相关配置等。
 *
 * @author Yun Jiao
 * @see AbstractMapDataSourceRoutingManager
 * @see BasicDataSource
 */
public class Dbcp2MapDataSourceRoutingManager extends AbstractMapDataSourceRoutingManager {
  @Override
  protected DataSource createDataSource(String name, JdbcOptions options) {
    DataSource defaultDataSource = getDefaultDataSource();

    if (defaultDataSource instanceof BasicDataSource basicDataSource) {
      BasicDataSource newDataSource = copyDataSource(basicDataSource);
      newDataSource.setDriverClassName(options.getDriverClassName());
      newDataSource.setUrl(options.getUrl());
      newDataSource.setUsername(options.getUsername());
      newDataSource.setPassword(options.getPassword());
      return newDataSource;
    } else {
      throw DataErrorCode.DATA_SOURCE_NOT_EXIST
          .throwed()
          .param("Expected", BasicDataSource.class.getSimpleName())
          .param("Actual", defaultDataSource.getClass().getSimpleName());
    }
  }

  // 创建数据源并复制属性
  private BasicDataSource copyDataSource(BasicDataSource original) {
    if (original == null) {
      return null;
    }

    BasicDataSource copy = new BasicDataSource();

    // ========== 2. 连接池核心参数 ==========
    copy.setInitialSize(original.getInitialSize()); // 初始连接数
    copy.setMaxTotal(original.getMaxTotal()); // 最大活跃连接数
    copy.setMaxIdle(original.getMaxIdle()); // 最大空闲连接数
    copy.setMinIdle(original.getMinIdle()); // 最小空闲连接数
    copy.setMaxWait(original.getMaxWaitDuration()); // 获取连接最大等待时间

    // ========== 3. 连接校验与超时配置 ==========
    copy.setValidationQuery(original.getValidationQuery()); // 连接校验SQL
    copy.setTestOnBorrow(original.getTestOnBorrow()); // 取出连接时校验
    copy.setTestOnReturn(original.getTestOnReturn()); // 归还连接时校验
    copy.setTestWhileIdle(original.getTestWhileIdle()); // 空闲时校验
    copy.setDurationBetweenEvictionRuns(original.getDurationBetweenEvictionRuns());
    copy.setMinEvictableIdle(original.getMinEvictableIdleDuration());

    // ========== 4. 事务与其他高级配置 ==========
    copy.setDefaultAutoCommit(original.getDefaultAutoCommit());
    copy.setDefaultReadOnly(original.getDefaultReadOnly());
    copy.setDefaultTransactionIsolation(original.getDefaultTransactionIsolation());

    return copy;
  }

  @Override
  protected void doShutdown(DataSource dataSource) throws SQLException {
    if (dataSource instanceof BasicDataSource basicDataSource) {
      basicDataSource.close();
    }
  }
}
