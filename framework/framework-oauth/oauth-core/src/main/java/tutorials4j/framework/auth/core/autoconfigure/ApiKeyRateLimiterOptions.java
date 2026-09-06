package tutorials4j.framework.auth.core.autoconfigure;

import java.time.Duration;
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
public class ApiKeyRateLimiterOptions {
  private String keyPrefix = "api_key_rate_limiter:";

  @NestedConfigurationProperty
  private HandlerInterceptorOptions interceptor = new HandlerInterceptorOptions();

  private ApiKeyLimiterRequestOptions defaultLimiter = new ApiKeyLimiterRequestOptions();
  private Map<String, ApiKeyLimiterRequestOptions> namedLimiter = new HashMap<>();

  @Data
  public static class ApiKeyLimiterRequestOptions {
    private Long maxRequestCount = 5L;
    private Duration timeWindow = Duration.ofSeconds(1);
  }
}
