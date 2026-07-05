package tutorials4j.framework.message.redis.bean;

import java.time.Duration;
import lombok.Builder;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ZSetMessageConfig(
    String queueName, Duration blockTimeout, Duration sleepWhenExcption) {

  public String getMainQueueName() {
    return "message:zset:" + queueName + ":main";
  }

  public String getProcessQueueName() {
    return "message:zset:" + queueName + ":process";
  }

  public String getDeadLetterQueueName() {
    return "message:zset:" + queueName + ":dead_letter";
  }
}
