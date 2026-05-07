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
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA)
public class DataProperties {

    private MybatisPlusOptions mybatisPlus = new MybatisPlusOptions();

    @Data
    public static class MybatisPlusOptions {
        private DbType dbType = DbType.POSTGRE_SQL;
    }
}
