package tutorials4j.framework.tenant.hibernate.autoconfigure;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.support.DataSourceRoutingManager;
import tutorials4j.framework.data.jdbc.routing.DataSourceRoutingManagerCreator;
import tutorials4j.framework.tenant.core.properties.TenantProperties;
import tutorials4j.framework.tenant.hibernate.DefaultCurrentTenantIdentifierResolver;
import tutorials4j.framework.tenant.hibernate.TenantDataSourceBasedMultiTenantConnectionProvider;

/**
 * 基于 Hibernate 的多租户自动配置类。
 *
 * <p>根据配置项 {@code tutorials4j.tenant.datasource.strategy} 的值选择租户隔离策略： 值为 {@code table}
 * 时启用共享表租户隔离（通过 {@link DefaultCurrentTenantIdentifierResolver} 解析当前租户标识），值为 {@code database}
 * 时启用独立库租户隔离（通过 {@link TenantDataSourceBasedMultiTenantConnectionProvider} 按租户路由数据源）。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class HibernateTenantConfiguration {
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[TENANT-HIBERNATE] Hibernate Configuration");
  }

  /**
   * 共享表模式的 Hibernate 租户配置。
   *
   * <p>当配置 {@code tutorials4j.tenant.datasource.strategy=table} 且存在 {@link DataSource} 时
   * 生效，注册当前租户标识解析器 Bean。
   *
   * @author Yun Jiao
   * @see DefaultCurrentTenantIdentifierResolver
   */
  @ConditionalOnBean(DataSource.class)
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT,
      name = "datasource.strategy",
      havingValue = "table")
  static class TableTenantConfiguration {

    /** 初始化日志记录。 */
    @PostConstruct
    public void postConstruct() {
      log.trace("[TENANT-HIBERNATE] Table Tenant Configuration");
    }

    /**
     * 注册默认的当前租户标识解析器 Bean，供 Hibernate 共享表模式解析当前租户标识。
     *
     * @return 默认当前租户标识解析器实例
     */
    @Bean
    @ConditionalOnMissingBean
    DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
      log.trace("[TENANT-HIBERNATE] Default Current Tenant Identifier Resolver");
      return new DefaultCurrentTenantIdentifierResolver();
    }
  }

  /**
   * 独立数据库模式的 Hibernate 租户配置。
   *
   * <p>当配置 {@code tutorials4j.tenant.datasource.strategy=database} 时生效，
   * 注册当前租户标识解析器与基于数据源路由的多租户连接提供器 Bean。
   *
   * @author Yun Jiao
   * @see TenantDataSourceBasedMultiTenantConnectionProvider
   */
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT,
      name = "datasource.strategy",
      havingValue = "database")
  static class DatabaseTenantConfiguration {
    /** 初始化日志记录。 */
    @PostConstruct
    public void postConstruct() {
      log.trace("[TENANT-HIBERNATE] Database Tenant Configuration");
    }

    /**
     * 注册默认的当前租户标识解析器 Bean，供 Hibernate 独立库模式解析当前租户标识。
     *
     * @return 默认当前租户标识解析器实例
     */
    @Bean
    @ConditionalOnMissingBean
    DefaultCurrentTenantIdentifierResolver defaultCurrentTenantIdentifierResolver() {
      log.trace("[TENANT-HIBERNATE] Default Current Tenant Identifier Resolver");
      return new DefaultCurrentTenantIdentifierResolver();
    }

    /**
     * 注册基于数据源路由的多租户连接提供器 Bean，并将租户 JDBC 配置注册到路由管理器。
     *
     * @param creator 数据源路由管理器创建器
     * @param properties 租户配置属性
     * @return 多租户连接提供器实例
     */
    @Bean
    @ConditionalOnMissingBean
    TenantDataSourceBasedMultiTenantConnectionProvider
        tenantDataSourceBasedMultiTenantConnectionProvider(
            DataSourceRoutingManagerCreator creator, TenantProperties properties) {
      log.trace("[TENANT-HIBERNATE] Tenant Data Source Based Multi Tenant Connection Provider");
      DataSourceRoutingManager dataSourceRoutingManager = creator.getInstance();

      properties
          .getDatasource()
          .getJdbc()
          .forEach((k, v) -> dataSourceRoutingManager.addRoutingJdbcOptions(k.toUpperCase(), v));
      return new TenantDataSourceBasedMultiTenantConnectionProvider(dataSourceRoutingManager);
    }
  }
}
