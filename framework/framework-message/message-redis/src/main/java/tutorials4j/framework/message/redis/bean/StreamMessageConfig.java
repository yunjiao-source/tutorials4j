package tutorials4j.framework.message.redis.bean;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record StreamMessageConfig(
    String queueName, int countPreRead, Duration blockTimeout, Duration sleepWhenExcption) {

  public String getMainQueueName() {
    return "message:stream:" + queueName + ":main";
  }
}
