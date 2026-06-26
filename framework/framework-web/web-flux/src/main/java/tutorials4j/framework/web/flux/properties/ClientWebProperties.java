package tutorials4j.framework.web.flux.properties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT)
public class ClientWebProperties {
  private boolean enabled = false;

  /** 默认请求头 */
  private Map<String, String> defaultHeaders = new HashMap<>();

  private RetryOptions retry = new RetryOptions();

  @Data
  public static class RetryOptions {
    private long maxAttempts = 3;
    private Duration minBackoff = Duration.ofSeconds(1);
    private Duration maxBackoff = Duration.ofSeconds(5);
  }
}
