package tutorials4j.framework.web.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * Web 安全功能的配置属性类，统一管理各安全拦截器的开关与选项配置。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_SECURITY)
public class SecurityWebProperties {
  /** 是否启用 Web 安全功能，默认 false */
  private boolean enabled = false;

  /** 幂等配置 */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions idempotent = new HandlerInterceptorOptions();

  /** 访问限制 */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions accessLimited = new HandlerInterceptorOptions();
}
