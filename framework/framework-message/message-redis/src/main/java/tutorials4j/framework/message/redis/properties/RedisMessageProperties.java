package tutorials4j.framework.message.redis.properties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.ExecutionOption;
import tutorials4j.framework.common.core.PropertiesConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_MESSAGE_REDIS)
public class RedisMessageProperties {
  private Map<String, QueueOptions> list = new HashMap<>();

  private Map<String, QueueOptions> zset = new HashMap<>();

  private StreamOptions stream = new StreamOptions();

  @Data
  public static class StreamOptions {
    private ExecutionOption execution = new ExecutionOption();
    private int countPerRead = 10;
    private Duration pollTimeout = Duration.ofSeconds(3);
    private Map<String, StreamQueueOptions> queues = new HashMap<>();
  }
}
