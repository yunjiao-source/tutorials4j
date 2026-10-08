package tutorials4j.toolkit.cache.caffeine.autoconfigure;

import java.time.Duration;
import lombok.Data;

@Data
public class NamedCacheOptions {
  private Duration expireAfterWrite = Duration.ofSeconds(30);
  private Duration expireAfterAccess;
  private boolean recordStats = false;
  private Integer initialCapacity;

  private int maximumSize = 2000;
}
