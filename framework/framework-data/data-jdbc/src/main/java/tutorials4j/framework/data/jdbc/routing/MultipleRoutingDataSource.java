package tutorials4j.framework.data.jdbc.routing;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import tutorials4j.framework.common.core.DefaultConsts;
import tutorials4j.framework.common.core.TenantContextHolder;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;

/**
 * 多租户动态路由数据源。
 *
 * <p>继承自 Spring 的 {@link AbstractRoutingDataSource}，根据当前线程上下文中保存的租户标识 （通过 {@link
 * TenantContextHolder#get()} 获取）动态选择目标数据源。
 *
 * <p>该数据源不直接维护所有数据源的映射，而是委托给 {@link DataSourceRoutingManager} 进行实际的
 * 数据源查找与创建。构造时需提供默认数据源，并预先将默认数据源注册到目标数据源映射中（键为 {@link DefaultConsts#DEFAULT_TENTANT_CODE}）。
 *
 * <p>典型使用场景：多租户架构中，每个租户拥有独立的数据库。租户标识可在请求入口处通过 {@code TenantContextHolder}
 * 设置，此后所有数据库操作都将自动路由到对应租户的数据源。
 *
 * @author Yun Jiao
 * @see AbstractRoutingDataSource
 * @see DataSourceRoutingManager
 * @see TenantContextHolder
 */
public class MultipleRoutingDataSource extends AbstractRoutingDataSource {
  private final DataSourceRoutingManager dataSourceRoutingManager;

  /**
   * 构造多路由数据源。
   *
   * @param dataSourceRoutingManager 数据源路由管理器，负责根据租户标识获取或创建对应的 {@link DataSource}
   * @param defaultDataSource 默认数据源，当无法从上下文中获取有效租户标识或路由管理器未找到对应数据源时使用
   */
  public MultipleRoutingDataSource(
      DataSourceRoutingManager dataSourceRoutingManager, DataSource defaultDataSource) {
    this.dataSourceRoutingManager = dataSourceRoutingManager;
    setDefaultTargetDataSource(defaultDataSource);
    setTargetDataSources(Map.of(DefaultConsts.DEFAULT_TENTANT_CODE, defaultDataSource));
  }

  @Override
  protected DataSource determineTargetDataSource() {
    Object lookupKey = determineCurrentLookupKey();
    if (lookupKey instanceof String lookupKeyStr) {
      return dataSourceRoutingManager.determineTargetDataSource(lookupKeyStr);
    }

    throw new IllegalStateException("不支持的多数据源键：" + lookupKey);
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return TenantContextHolder.get();
  }
}
