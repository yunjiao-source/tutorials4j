package tutorials4j.framework.message.redis.properties;

import java.time.Duration;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class QueueOptions {
  private Duration blockTimeout = Duration.ofSeconds(3);
}
