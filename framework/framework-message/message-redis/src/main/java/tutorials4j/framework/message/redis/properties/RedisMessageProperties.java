package tutorials4j.framework.message.redis.properties;

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
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_MESSAGE_REDIS)
public class RedisMessageProperties {
  private Map<String, ListQueueOptions> listQueues = new HashMap<>();

  @Data
  public static class ListQueueOptions {
    private Duration blockTimeout = Duration.ofSeconds(3);
    private Duration sleepWhenException = Duration.ofSeconds(5);
    private Duration sleepWhenNoData = Duration.ofSeconds(5);
  }
}
