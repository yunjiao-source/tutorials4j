package tutorials4j.framework.message.redis.zset;

import java.time.Duration;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ZSetMessageConfig(
    String name, String delayQueueName, String processQueueName, Duration blockTimeout) {

  public void validate() {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("name must not be null or empty");
    }

    if (StringUtils.isBlank(delayQueueName)) {
      throw new IllegalArgumentException("delayQueueName must not be null or empty");
    }

    if (StringUtils.isBlank(processQueueName)) {
      throw new IllegalArgumentException("processQueueName must not be null or empty");
    }

    if (blockTimeout == null) {
      throw new IllegalArgumentException("blockTimeout must not be null");
    }
  }
}
