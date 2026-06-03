package tutorials4j.framework.web.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_SECURITY_XSS)
public class XssWebProperties {
  private boolean enabled = false;

  /** xss攻击配置 */
  @NestedConfigurationProperty
  private ServletFilterOptions filter =
      new ServletFilterOptions(
          new String[] {"/*"},
          1,
          "xssRequestFilter",
          ServletFilterOptions.DEFAULT_DISPATCHER_TYPES);
}
