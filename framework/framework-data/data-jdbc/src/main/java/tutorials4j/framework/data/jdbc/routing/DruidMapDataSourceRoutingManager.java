package tutorials4j.framework.data.jdbc.routing;

import com.alibaba.druid.pool.DruidDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.common.core.exception.BaseErrorCode;
import tutorials4j.framework.data.core.exception.DataErrorCode;

/**
 * 基于 Alibaba Druid 连接池的数据源路由管理器实现。
 *
 * <p>当默认数据源为 {@link DruidDataSource} 时，通过复制其配置并为每个租户创建独立的 {@code DruidDataSource} 实例。 复制完成后调用
 * {@link DruidDataSource#init()} 方法初始化数据源。
 *
 * @author Yun Jiao
 * @see AbstractMapDataSourceRoutingManager
 * @see DruidDataSource
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
        throw BaseErrorCode.CHECK_EXCEPTION.throwed(e);
      }
    } else {
      throw DataErrorCode.DATA_SOURCE_NOT_EXIST
          .throwed()
          .param("Expected", DruidDataSource.class.getSimpleName())
          .param("Actual", defaultDataSource.getClass().getSimpleName());
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
    dataSource.setFilters(String.join(",", original.getFilterClassNames()));

    return dataSource;
  }

  @Override
  protected void doShutdown(DataSource dataSource) throws SQLException {
    if (dataSource instanceof DruidDataSource druidDataSource) {
      druidDataSource.close();
    }
  }
}
