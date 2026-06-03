package tutorials4j.framework.web.security.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_SECURITY)
public class SecurityWebProperties {
  /** 幂等配置 */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions idempotent = new HandlerInterceptorOptions();

  /** 访问限制 */
  @NestedConfigurationProperty
  private HandlerInterceptorOptions accessLimited = new HandlerInterceptorOptions();

  private SignatureOptions signature = new SignatureOptions();

  @Data
  public static class SignatureOptions {
    @NestedConfigurationProperty
    private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();

    private Map<String, String> keys = new HashMap<>();
  }
}
