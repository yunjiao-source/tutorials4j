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
public class StreamQueueOptions extends QueueOptions {
  private String listenerBeanName;
  private String consumerGroup;
  private int consumerCount = 1;
  private boolean autoAck = true;
  private boolean cancelOnError = false;
  private Idempotent idempotent = new Idempotent();

  @Data
  public static class Idempotent {
    private boolean enabled = true;
    private Duration expiredTime = Duration.ofDays(3);
  }
}
