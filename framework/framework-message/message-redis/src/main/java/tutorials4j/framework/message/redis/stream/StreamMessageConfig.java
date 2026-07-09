package tutorials4j.framework.message.redis.stream;

import java.time.Duration;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record StreamMessageConfig(
    String name,
    String queueName,
    int countPreRead,
    Duration sleepTimeWhenException,
    Duration blockTimeout,
    String consumerGroup,
    Duration retentionTime) {

  public void validate() {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("name must not be null or empty");
    }

    if (StringUtils.isBlank(queueName)) {
      throw new IllegalArgumentException("queueName must not be null or empty");
    }

    if (StringUtils.isBlank(consumerGroup)) {
      throw new IllegalArgumentException("consumerGroup must not be null or empty");
    }

    if (countPreRead <= 0) {
      throw new IllegalArgumentException("countPreRead must not be greater than zero");
    }

    if (sleepTimeWhenException == null) {
      throw new IllegalArgumentException("sleepTimeWhenException must not be null");
    }

    if (retentionTime == null) {
      throw new IllegalArgumentException("retentionTime must not be null");
    }
  }
}
