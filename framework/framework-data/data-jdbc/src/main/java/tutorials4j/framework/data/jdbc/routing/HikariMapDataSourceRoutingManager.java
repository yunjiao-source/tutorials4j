package tutorials4j.framework.data.jdbc.routing;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.data.core.exception.DataErrorCode;

/**
 * 基于 HikariCP 连接池的数据源路由管理器实现。
 *
 * <p>当默认数据源为 {@link HikariDataSource} 时，通过 {@link HikariDataSource#copyStateTo(HikariConfig)} 复制配置，
 * 并为每个租户创建独立的 {@code HikariDataSource} 实例。
 *
 * @author Yun Jiao
 * @see AbstractMapDataSourceRoutingManager
 * @see HikariDataSource
 * @see HikariConfig
 */
public class HikariMapDataSourceRoutingManager extends AbstractMapDataSourceRoutingManager {
  /**
   * 基于默认 Hikari 数据源复制配置并创建指定路由名称的新数据源。
   *
   * @param tenant 路由名称（租户标识）
   * @param options JDBC 连接选项
   * @return 新创建的数据源
   */
  @Override
  protected DataSource createDataSource(String tenant, JdbcOptions options) {
    DataSource defaultDataSource = getDefaultDataSource();

    if (defaultDataSource instanceof HikariDataSource hikariDataSource) {
      final HikariConfig hikariConfig = new HikariConfig();
      hikariDataSource.copyStateTo(hikariConfig);
      hikariConfig.setDriverClassName(options.getDriverClassName());
      hikariConfig.setJdbcUrl(options.getUrl());
      hikariConfig.setUsername(options.getUsername());
      hikariConfig.setPassword(options.getPassword());

      return new HikariDataSource(hikariConfig);
    } else {
      throw DataErrorCode.DATA_SOURCE_NOT_EXIST
          .throwed()
          .param("Expected", HikariDataSource.class.getSimpleName())
          .param("Actual", defaultDataSource.getClass().getSimpleName());
    }
  }

  /**
   * 关闭 Hikari 数据源。
   *
   * @param dataSource 需要关闭的数据源
   * @throws SQLException 关闭数据源发生异常时抛出
   */
  @Override
  protected void doShutdown(DataSource dataSource) throws SQLException {
    if (dataSource instanceof HikariDataSource hikariDataSource) {
      hikariDataSource.close();
    }
  }
}
