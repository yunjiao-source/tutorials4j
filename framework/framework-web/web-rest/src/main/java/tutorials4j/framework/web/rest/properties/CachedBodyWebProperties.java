package tutorials4j.framework.web.rest.properties;

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
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CACHAED_BODY)
public class CachedBodyWebProperties {
  private boolean enabled = false;

  /** 缓存请求体配置属性 */
  @NestedConfigurationProperty private ServletFilterOptions filter = new ServletFilterOptions();
}
