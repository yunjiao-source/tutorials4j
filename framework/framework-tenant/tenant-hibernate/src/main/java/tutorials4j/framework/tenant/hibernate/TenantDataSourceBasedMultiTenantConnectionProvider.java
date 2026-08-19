package tutorials4j.framework.tenant.hibernate;

import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;

/**
 * 基于数据源路由的多租户连接提供器：根据租户标识从路由管理器获取对应租户的数据源，供 Hibernate 独立数据库模式使用。
 *
 * <p>同时实现 {@link HibernatePropertiesCustomizer} 接口，自动将自身注册到 Hibernate 的多租户连接提供器配置中。
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class TenantDataSourceBasedMultiTenantConnectionProvider
    extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String>
    implements HibernatePropertiesCustomizer {
  private final DataSourceRoutingManager manager;

  /**
   * 选择默认数据源，用于无租户上下文时的连接获取。
   *
   * @return 默认数据源
   */
  @Override
  protected DataSource selectAnyDataSource() {
    return manager.getDefaultDataSource();
  }

  /**
   * 根据租户标识路由到对应租户的数据源。
   *
   * @param tenantIdentifier 租户标识
   * @return 该租户对应的数据源
   */
  @Override
  protected DataSource selectDataSource(String tenantIdentifier) {
    return manager.determineTargetDataSource(tenantIdentifier);
  }

  /**
   * 将当前连接提供器注册到 Hibernate 的配置属性中。
   *
   * @param hibernateProperties Hibernate 属性集合，会被 Spring Boot 的 JPA 属性合并
   */
  @Override
  public void customize(Map<String, Object> hibernateProperties) {
    hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
  }
}
