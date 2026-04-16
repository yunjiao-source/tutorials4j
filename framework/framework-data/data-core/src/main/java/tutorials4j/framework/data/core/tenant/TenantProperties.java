package tutorials4j.framework.data.core.tenant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.data.core.DataPropertiesConsts;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = DataPropertiesConsts.PROPERTY_PREFIX_DATA_TENANT)
public class TenantProperties {
    private String[] pathPatterns = new String[]{"/**"};
    private Map<String, DataSourceProperties> datasource = new HashMap<>();

    /**
     * 数据源属性
     */
    @Data
    public static class DataSourceProperties {
        private String driverClassName;

        private String url;

        private String username;

        private String password;
    }
}
