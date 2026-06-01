package tutorials4j.framework.web.rest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.spring.web.ServletFilterOptions;

/**
 * Web Http 属性
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_REST)
public class RestWebProperties {
  /** 缓存请求体配置属性 */
  @NestedConfigurationProperty
  private ServletFilterOptions cachedBody =
      new ServletFilterOptions(
          new String[] {},
          1,
          "cachedBodyRequestFilter",
          ServletFilterOptions.DEFAULT_DISPATCHER_TYPES);
}
