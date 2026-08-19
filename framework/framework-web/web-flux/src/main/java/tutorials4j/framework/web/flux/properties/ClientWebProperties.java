package tutorials4j.framework.web.flux.properties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * Web 客户端配置属性。
 *
 * <p>包含客户端定制功能的启用开关、默认请求头以及重试选项等。
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_WEB_CLIENT)
public class ClientWebProperties {
  /** 是否启用 Web 客户端定制功能。 */
  private boolean enabled = false;

  /** 默认请求头 */
  private Map<String, String> defaultHeaders = new HashMap<>();

  /** 重试选项 */
  private RetryOptions retry = new RetryOptions();

  /** 重试选项配置。 */
  @Data
  public static class RetryOptions {
    /** 最大重试次数。 */
    private long maxAttempts = 3;

    /** 最小退避间隔。 */
    private Duration minBackoff = Duration.ofSeconds(1);

    /** 最大退避间隔。 */
    private Duration maxBackoff = Duration.ofSeconds(5);
  }
}
