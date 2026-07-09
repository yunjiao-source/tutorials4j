package tutorials4j.framework.message.redis.list;

import java.time.Duration;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ListMessageConfig(
    String name, String queueName, Duration blockTimeout, Duration sleepTimeWhenException) {

  public void validate() {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("name must not be null or empty");
    }
    if (StringUtils.isBlank(queueName)) {
      throw new IllegalArgumentException("queueName must not be null or empty");
    }

    if (blockTimeout == null) {
      throw new IllegalArgumentException("blockTimeout must not be null");
    }

    if (sleepTimeWhenException == null) {
      throw new IllegalArgumentException("sleepTimeWhenException must not be null");
    }
  }
}
