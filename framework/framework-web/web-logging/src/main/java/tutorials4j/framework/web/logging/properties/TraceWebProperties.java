package tutorials4j.framework.web.logging.properties;

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
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_TRACE)
public class TraceWebProperties {
  private boolean enabled = false;

  /** 链路追踪 Servlet 过滤器配置属性。 */
  @NestedConfigurationProperty private ServletFilterOptions filter = new ServletFilterOptions();
}
