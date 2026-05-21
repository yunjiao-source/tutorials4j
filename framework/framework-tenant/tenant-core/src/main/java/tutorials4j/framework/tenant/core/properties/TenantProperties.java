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
 * 租户配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT)
public class TenantProperties {

  @NestedConfigurationProperty
  private HandlerInterceptorOptions path = new HandlerInterceptorOptions();

  private DataSourceOptions datasource = new DataSourceOptions();

  private MybatisPlusOptions mybatisPlus = new MybatisPlusOptions();

  /** 数据源属性 */
  @Data
  public static class DataSourceOptions {
    /** 租户策略，默认：独立数据库(DATABASE) */
    private TenantStrategy strategy = TenantStrategy.DATABASE;

    /** 数据源属性，可以配置多个 */
    private Map<String, JdbcOptions> jdbc = new HashMap<>();
  }

  /** mybatis配置 */
  @Data
  public static class MybatisPlusOptions {
    private String[] ignoreTable = new String[] {};
  }
}
