package tutorials4j.framework.message.redis.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
  private Map<String, QueueOptions> listQueues = new HashMap<>();

  private Map<String, QueueOptions> zsetQueues = new HashMap<>();

  private Map<String, StreamQueueOptions> streamQueues = new HashMap<>();

  @Getter
  @Setter
  public static class StreamQueueOptions extends QueueOptions {
    private int countPerRead = 10;
  }
}
