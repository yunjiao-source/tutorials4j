package tutorials4j.framework.data.core.properties;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_MYBATIS_PLUS)
public class MybatisPlusDataProperties {
  private DbType dbType = DbType.POSTGRE_SQL;
  private InterceptorOptions interceptors = new InterceptorOptions();

  @Data
  public static class InterceptorOptions {
    private boolean pagination = true;
    private boolean optimisticLocker = true;
    private boolean blockAttack = true;
  }
}
