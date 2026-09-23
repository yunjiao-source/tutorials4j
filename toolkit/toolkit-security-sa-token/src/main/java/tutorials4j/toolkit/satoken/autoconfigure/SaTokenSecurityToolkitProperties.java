package tutorials4j.toolkit.satoken.autoconfigure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_SECURITY_SA_TOKEN)
public class SaTokenSecurityToolkitProperties {
  private FilterOptions filter = new FilterOptions();
  private InterceptorOptions interceptor = new InterceptorOptions();
  private PermissionOptions permission = new PermissionOptions();

  @Data
  public static class PermissionOptions {
    private String roleKeyPrefix = "satoken:role-find-permission:";
    private Duration roleKeyExpired = Duration.ofMinutes(30);

    private String userKeyPrefix = "satoken:user-find-role:";
    private Duration userKeyDuration = Duration.ofMinutes(30);
  }

  @Data
  public static class FilterOptions {
    private List<String> includeUrls = List.of("/this-attribute-must-be-configured-manually/**");
    private List<String> excludeUrls = new ArrayList<>();
    private List<String> whiteUrls = new ArrayList<>();
    private List<String> blockUrls = new ArrayList<>();
  }

  @Data
  public static class InterceptorOptions {
    private boolean checkLogin = false;
    private boolean isAnnotation = true;
    private List<String> includePathPatterns =
        List.of("/this-attribute-must-be-configured-manually/**");
    private List<String> excludePathPatterns = new ArrayList<>();
  }
}
