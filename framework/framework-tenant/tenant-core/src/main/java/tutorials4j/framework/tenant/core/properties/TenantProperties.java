package tutorials4j.framework.tenant.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.support.HandlerInterceptorOptions;

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


}
