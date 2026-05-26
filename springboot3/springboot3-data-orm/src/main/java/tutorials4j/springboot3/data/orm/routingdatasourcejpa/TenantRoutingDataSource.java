package tutorials4j.springboot3.data.orm.routingdatasourcejpa;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 自定义路由数据源
 *
 * @author Yun Jiao
 */
public class TenantRoutingDataSource extends AbstractRoutingDataSource {
  @Override
  protected Object determineCurrentLookupKey() {
    // 从ThreadLocal中获取当前租户标识，决定使用哪个数据源
    return DataSourceContextHolder.getTenantId();
  }
}
