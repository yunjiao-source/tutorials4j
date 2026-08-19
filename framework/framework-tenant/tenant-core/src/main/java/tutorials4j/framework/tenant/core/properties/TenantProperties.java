package tutorials4j.framework.tenant.core.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;
import tutorials4j.framework.tenant.core.TenantStrategy;

/**
 * 租户模块配置属性：通过 {@code spring.tenant} 前缀绑定，包含拦截器路径、数据源与 MyBatis-Plus 相关配置。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT)
public class TenantProperties {

  /** 租户拦截器的路径匹配配置 */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions path = new HandlerInterceptorOptions();

  /** 数据源相关配置 */
  private DataSourceOptions datasource = new DataSourceOptions();

  /** MyBatis-Plus 相关配置 */
  private MybatisPlusOptions mybatisPlus = new MybatisPlusOptions();

  /**
   * 数据源配置选项：包含租户策略与各租户的数据源连接属性。
   *
   * @author Yun Jiao
   */
  @Data
  public static class DataSourceOptions {
    /** 租户策略，默认：独立数据库(DATABASE) */
    private TenantStrategy strategy = TenantStrategy.DATABASE;

    /** 各租户的数据源连接配置，key 为租户标识 */
    private Map<String, JdbcOptions> jdbc = new HashMap<>();
  }

  /**
   * MyBatis-Plus 相关配置选项。
   *
   * @author Yun Jiao
   */
  @Data
  public static class MybatisPlusOptions {

    /** 需要忽略租户隔离的表名列表 */
    private String[] ignoreTable = new String[] {};
  }
}
