package tutorials4j.framework.data.jdbc.routing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;
import tutorials4j.framework.data.core.exception.DataSourceNameNotFoundException;

/**
 * 数据源路由管理的抽象基类。
 *
 * <p>实现了默认数据源存储、路由 {@link JdbcOptions} 的添加与管理，并提供了根据名称创建具体数据源的模板方法。 子类需实现 {@link
 * #createDataSource(String, JdbcOptions)} 以完成特定连接池的数据源创建。
 *
 * @author Yun Jiao
 * @see DataSourceRoutingManager
 * @see JdbcOptions
 */
@Slf4j
public abstract class AbstractDataSourceRoutingManager implements DataSourceRoutingManager {
  /** 默认数据源 */
  private DataSource defaultDataSource;

  private final Map<String, JdbcOptions> jdbcOptionsMap = new HashMap<>();

  @Override
  public DataSource getDefaultDataSource() {
    return defaultDataSource;
  }

  public void setDefaultDataSource(DataSource defaultDataSource) {
    Assert.notNull(defaultDataSource, "defaultDataSource must not be null");
    this.defaultDataSource = defaultDataSource;
  }

  public Map<String, JdbcOptions> getJdbcOptionsMap() {
    return Collections.unmodifiableMap(jdbcOptionsMap);
  }

  /**
   * 添加一条路由 JDBC 配置。
   *
   * @param name 路由名称，不能为 {@code null}
   * @param jdbcOptions JDBC 连接选项，不能为 {@code null}
   * @throws IllegalArgumentException 如果任一参数为 {@code null}
   */
  @Override
  public void addRoutingJdbcOptions(String name, JdbcOptions jdbcOptions) {
    Assert.notNull(name, "name must not be null");
    Assert.notNull(jdbcOptions, "jdbcOptions must not be null");

    jdbcOptionsMap.put(name, jdbcOptions);
  }

  /**
   * 初始化路由管理器。
   *
   * @param dataSource 默认数据源，不能为 {@code null}
   * @param jdbcOptionsMap 初始路由配置映射，不能为 {@code null}
   * @throws IllegalArgumentException 如果任一参数为 {@code null}
   */
  public void init(DataSource dataSource, Map<String, JdbcOptions> jdbcOptionsMap) {
    Assert.notNull(dataSource, "dataSource must not be null");
    Assert.notNull(jdbcOptionsMap, "jdbcOptionsMap must not be null");

    this.defaultDataSource = dataSource;
    this.jdbcOptionsMap.putAll(jdbcOptionsMap);
  }

  /**
   * 根据路由名称创建数据源。
   *
   * <p>先从 {@link #getJdbcOptionsMap()} 中获取对应的 {@code JdbcOptions}， 若不存在则抛出 {@link
   * DataSourceNameNotFoundException}。
   *
   * @param name 路由名称，不能为 {@code null}
   * @return 新创建的数据源
   * @throws DataSourceNameNotFoundException 如果未找到对应名称的 JdbcOptions
   */
  protected DataSource createDataSource(String name) {
    JdbcOptions jdbcOptions = jdbcOptionsMap.get(name);
    if (jdbcOptions == null) {
      throw new DataSourceNameNotFoundException(name);
    }
    if (log.isDebugEnabled()) {
      log.debug("[DATA-JDBC] 创建指定数据源：name = {}, url = {}", name, jdbcOptions.getUrl());
    }
    return createDataSource(name, jdbcOptions);
  }

  /**
   * 根据路由名称和 JDBC 配置创建具体的数据源实例。
   *
   * <p>由子类实现，用于不同连接池（如 HikariCP、DBCP2、Druid）的数据源创建逻辑。
   *
   * @param name 路由名称
   * @param jdbcOptions JDBC 连接选项
   * @return 新创建的数据源
   */
  protected abstract DataSource createDataSource(String name, JdbcOptions jdbcOptions);
}
