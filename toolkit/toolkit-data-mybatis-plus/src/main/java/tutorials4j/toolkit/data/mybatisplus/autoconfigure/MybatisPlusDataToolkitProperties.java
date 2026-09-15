package tutorials4j.toolkit.data.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_DATA_MYBATIS_PLUS)
public class MybatisPlusDataToolkitProperties {
  private DbType dbType = DbType.MYSQL;
}
