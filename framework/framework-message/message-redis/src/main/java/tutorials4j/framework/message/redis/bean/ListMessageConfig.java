package tutorials4j.framework.message.redis.bean;

import java.time.Duration;
import lombok.Builder;
import tutorials4j.framework.common.core.ExecutionOption;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ListMessageConfig(
    String queueName,
    Duration blockTimeout,
    Duration sleepWhenExcption,
    ExecutionOption execution) {

  public String getMainQueueName() {
    return "message:list:" + queueName + ":main";
  }

  public String getProcessQueueName() {
    return "message:list:" + queueName + ":process";
  }

  public String getDeadLetterQueueName() {
    return "message:list:" + queueName + ":dead_letter";
  }
}
