package tutorials4j.framework.web.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * XSS 防护功能的配置属性类，包含功能开关与过滤器选项。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_XSS)
public class XssWebProperties {
  /** 是否启用 XSS 防护，默认 false */
  private boolean enabled = false;

  /** xss攻击配置 */
  @NestedConfigurationProperty private ServletFilterOptions filter = new ServletFilterOptions();
}
