package tutorials4j.toolkit.cache.redis.autoconfigure;

import java.time.Duration;
import lombok.Data;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Data
public class NamedCacheOptions {
  private Duration timeToLive = Duration.ofSeconds(30);
  private boolean cachingNullValues = true;
  private String keyPrefix;
}
