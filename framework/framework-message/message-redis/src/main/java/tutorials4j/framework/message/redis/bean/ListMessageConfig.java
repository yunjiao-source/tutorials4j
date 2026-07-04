package tutorials4j.framework.message.redis.bean;

import java.time.Duration;
import lombok.Builder;
import org.springframework.util.Assert;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Builder
public record ListMessageConfig(
    String queueName, Duration blockTimeout, Duration sleepWhenExcption, Duration sleepWhenNoData) {

  public String getMainQueueName() {
    return "message:" + queueName + ":main";
  }

  public String getProcessQueueName() {
    return "message:" + queueName + ":process";
  }

  public String getDeadLetterQueueName() {
    return "message:" + queueName + ":dead_letter";
  }

  public static ListMessageConfig defaultConfig(String queueName) {
    Assert.hasText(queueName, "queueName must not be null or empty");
    return ListMessageConfig.builder()
        .queueName(queueName)
        .blockTimeout(Duration.ofSeconds(5L))
        .build();
  }
}
