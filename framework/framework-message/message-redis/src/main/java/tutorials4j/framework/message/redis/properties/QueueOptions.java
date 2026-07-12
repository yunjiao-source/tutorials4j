package tutorials4j.framework.message.redis.properties;

import java.time.Duration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
public class QueueOptions {
  private Duration blockTimeout = Duration.ofSeconds(3);
  private Duration sleepTimeWhenException = Duration.ofSeconds(3);
  private StreamQueueOptions stream = new StreamQueueOptions();
  private RetryOptions retry = new RetryOptions();

  @Data
  public static class RetryOptions {
    private boolean enabled = true;
    private int maxCount = 3;
    private Duration expireTime = Duration.ofMinutes(1);
    private Duration delayTime = Duration.ofSeconds(3);
  }

  @Data
  public static class StreamQueueOptions {
    private int countPreRead = 10;
    private Duration retentionTime = Duration.ofDays(30);
  }
}
