package tutorials4j.framework.oauth.satoken.autoconfigure;

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
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_OAUTH_SA_TOKEN)
public class SaTokenOAuthProperties {
  private boolean logForSlf4j = false;
  private String ssoLoginUrl;

  @NestedConfigurationProperty
  private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();

  private SaServletFilterOptions filter = new SaServletFilterOptions();

  @Data
  public static class SaServletFilterOptions {
    private String[] includePatterns = new String[] {"/demo/**"};
    private String[] excludePatterns;
  }
}
