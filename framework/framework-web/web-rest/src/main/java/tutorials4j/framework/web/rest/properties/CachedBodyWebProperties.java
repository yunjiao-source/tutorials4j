package tutorials4j.framework.web.rest.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * 缓存请求体功能配置属性。
 *
 * <p>包含缓存请求体的启用开关与 Servlet 过滤器通用配置。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CACHED_BODY)
public class CachedBodyWebProperties {
  /** 是否启用缓存请求体功能。 */
  private boolean enabled = false;

  /** 缓存请求体配置属性 */
  @NestedConfigurationProperty private ServletFilterOptions filter = new ServletFilterOptions();
}
