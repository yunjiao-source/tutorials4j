package tutorials4j.framework.tenant.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.JdbcOptions;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.tenant.core.TenantStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT_DATASOURCE)
public class TenantDataSourceProperties {
    /**
     * 租户策略，默认：独立数据库(DATABASE)
     */
    private TenantStrategy strategy = TenantStrategy.HIBERNATE_DATABASE;

    /**
     * 数据源属性，可以配置多个
     */
    private Map<String, JdbcOptions> jdbc = new HashMap<>();

}
