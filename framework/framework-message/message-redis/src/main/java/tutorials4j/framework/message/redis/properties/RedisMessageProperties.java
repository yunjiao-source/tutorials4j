package tutorials4j.framework.message.redis.properties;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.framework.common.core.PropertiesConsts;
import tutorials4j.framework.message.redis.bean.RedisMessageType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertiesConsts.PROPERTY_PREFIX_MESSAGE_REDIS)
public class RedisMessageProperties {

  @Data
  public static class QueueOptions {
    private String keyPrefix = "message:";
    private RedisMessageType messageType = RedisMessageType.list;
    private Duration blockTimeout = Duration.ofSeconds(3);
    private Duration sleepTimeWhenException = Duration.ofSeconds(3);
  }

  @Data
  public static class RetryOptions {
    private boolean enabled = false;
    private int maxCount = 3;
    private Duration expireTime = Duration.ofMinutes(3);
  }
}
