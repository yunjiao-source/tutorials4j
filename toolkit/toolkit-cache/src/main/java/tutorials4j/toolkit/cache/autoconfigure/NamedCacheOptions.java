package tutorials4j.toolkit.cache.autoconfigure;

import java.time.Duration;
import lombok.Data;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
public class NamedCacheOptions {
  private Duration timeToLive = Duration.ofSeconds(30);

  private int initialCapacity = 10000;

  private int maximumSize = 20000;
}
