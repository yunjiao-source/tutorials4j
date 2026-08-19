package tutorials4j.framework.tenant.mybatis.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.SqlSessionFactoryBeanCustomizer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.data.jdbc.routing.DataSourceRoutingManagerCreator;
import tutorials4j.framework.data.jdbc.routing.MultipleRoutingDataSource;
import tutorials4j.framework.data.mybatis.customizer.MybatisPlusInterceptorCustomizer;
import tutorials4j.framework.tenant.core.properties.TenantProperties;
import tutorials4j.framework.tenant.mybatis.SimpleTenantLineInterceptorCustomizer;

/**
 * MyBatis Plus 多租户自动配置类。
 *
 * <p>根据配置项 {@code tutorials4j.tenant.datasource.strategy} 的值选择租户隔离策略： 值为 {@code table}
 * 时启用共享表租户隔离（通过租户拦截器注入租户条件），值为 {@code database} 时启用独立库租户隔离（通过路由数据源切换数据库）。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class MybatisPlusTenantConfiguration {
  /** 初始化日志记录。 */
  @PostConstruct
  public void postConstruct() {
    log.trace("[TENANT-MYBATIS-PLUS] Mybatis Plus Configuration");
  }

  /**
   * 共享表模式的 MyBatis Plus 租户配置。
   *
   * <p>当配置 {@code tutorials4j.tenant.datasource.strategy=table} 时生效， 通过租户行拦截器为 SQL
   * 注入租户条件，实现共享表数据隔离。
   *
   * @author Yun Jiao
   */
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT,
      name = "datasource.strategy",
      havingValue = "table")
  static class TableTenantConfiguration {

    /** 初始化日志记录。 */
    @PostConstruct
    public void postConstruct() {
      log.trace("[TENANT-MYBATIS-PLUS] Table Tenant Configuration");
    }

    /**
     * 注册租户行拦截器定制器 Bean，用于共享表模式的租户 SQL 注入。
     *
     * @param properties 租户配置属性
     * @return 租户拦截器定制器实例
     */
    @Bean
    MybatisPlusInterceptorCustomizer defaultTenantLineInterceptorCustomizer(
        TenantProperties properties) {
      log.trace("[TENANT-MYBATIS-PLUS] Simple Tenant Line Interceptor Customizer");
      return new SimpleTenantLineInterceptorCustomizer(properties);
    }
  }

  /**
   * 独立数据库模式的 MyBatis Plus 租户配置。
   *
   * <p>当配置 {@code tutorials4j.tenant.datasource.strategy=database} 时生效， 通过路由数据源切换租户数据库，实现独立库数据隔离。
   *
   * @author Yun Jiao
   */
  @ConditionalOnProperty(
      prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT,
      name = "datasource.strategy",
      havingValue = "database")
  static class DatabaseTenantConfiguration {

    /** 初始化日志记录。 */
    @PostConstruct
    public void postConstruct() {
      log.trace("[TENANT-MYBATIS-PLUS] Database Tenant Configuration");
    }

    /**
     * 注册 SqlSessionFactoryBean 定制器 Bean，为 MyBatis 工厂设置基于路由的数据源。
     *
     * @param creator 数据源路由管理器创建器
     * @return SqlSessionFactoryBean 定制器实例
     */
    @Bean
    SqlSessionFactoryBeanCustomizer databaseSqlSessionFactoryBeanCustomizer(
        DataSourceRoutingManagerCreator creator) {
      log.trace("[TENANT-MYBATIS-PLUS] Database SqlSession Factory Bean Customizer ");
      return factoryBean -> {
        factoryBean.setDataSource(
            new MultipleRoutingDataSource(creator.getInstance(), creator.getDefaultDataSource()));
      };
    }
  }
}
