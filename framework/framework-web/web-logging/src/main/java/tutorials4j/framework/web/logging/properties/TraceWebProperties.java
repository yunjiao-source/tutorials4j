package tutorials4j.framework.web.logging.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * 链路追踪（Trace）功能配置属性。
 *
 * <p>包含链路追踪的启用开关与 Servlet 过滤器通用配置。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_TRACE)
public class TraceWebProperties {
  /** 是否启用链路追踪功能。 */
  private boolean enabled = false;

  /** 链路追踪 Servlet 过滤器配置属性。 */
  @NestedConfigurationProperty private ServletFilterOptions filter = new ServletFilterOptions();
}
