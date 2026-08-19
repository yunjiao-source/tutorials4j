package tutorials4j.framework.data.mybatis.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * MyBatis Plus 数据访问组件配置属性。
 *
 * <p>对应配置前缀 {@value PropertiesConsts#PROPERTY_PREFIX_DATA_MYBATIS_PLUS}，用于配置 数据库类型以及各拦截器的启用开关，由框架的
 * MyBatis Plus 自动配置类读取并据此装配组件。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS)
public class MybatisPlusDataProperties {
  /** 数据库类型，用于分页拦截器生成对应的分页 SQL，默认 PostgreSQL。 */
  private DbType dbType = DbType.POSTGRE_SQL;

  /** 各内置拦截器的启用选项。 */
  private InterceptorOptions interceptors = new InterceptorOptions();

  /**
   * MyBatis Plus 内置拦截器的启用选项。
   *
   * @author Yun Jiao
   */
  @Data
  public static class InterceptorOptions {
    /** 是否启用分页拦截器，默认开启。 */
    private boolean pagination = true;

    /** 是否启用乐观锁拦截器，默认开启。 */
    private boolean optimisticLocker = true;

    /** 是否启用防全表更新/删除拦截器，默认开启。 */
    private boolean blockAttack = true;
  }
}
