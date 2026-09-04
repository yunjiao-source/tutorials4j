package tutorials4j.framework.auth.core.autoconfigure;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import tutorials4j.framework.common.core.bean.HandlerInterceptorOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class ApiKeyOptions {
  private String keyPrefix = "oauth:apikey:";

  @NestedConfigurationProperty
  private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();

  private ApiKeyLimiterOptions defaultLimiter = new ApiKeyLimiterOptions();
  private Map<String, ApiKeyLimiterOptions> namedLimiter = new HashMap<>();

  @Data
  public static class ApiKeyLimiterOptions {
    private Integer maxRequestsPerMinute = 100;
    private Integer maxRequestsPerDay = 10000;
  }
}
