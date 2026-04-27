package tutorials4j.framework.tenant.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * 租户配置
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_TENANT)
public class TenantProperties {
    /**
     * 请求路径列表，添加租户代码信息到线程上下文(ThreadLocal)中
     */
    private String[] pathPatterns = new String[]{"/**"};


}
