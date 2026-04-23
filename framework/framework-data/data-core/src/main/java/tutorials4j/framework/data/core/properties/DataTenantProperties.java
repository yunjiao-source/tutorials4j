package tutorials4j.framework.data.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.lang.PropertiesConsts;
import tutorials4j.framework.data.core.tenant.TenantType;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_DATA_TENANT)
public class DataTenantProperties {
    /**
     * 请求路径列表，添加租户代码信息到线程上下文(ThreadLocal)中
     */
    private String[] pathPatterns = new String[]{"/**"};

    /**
     * 租户类型，默认：独立数据库(DATABASE)
     */
    private TenantType type = TenantType.DATABASE;

    /**
     * 数据源属性，可以配置多个
     */
    private Map<String, DataSourceOptions> datasource = new HashMap<>();

    /**
     * 数据源属性
     */
    @Data
    public static class DataSourceOptions {
        private String driverClassName;

        private String url;

        private String username;

        private String password;
    }
}
