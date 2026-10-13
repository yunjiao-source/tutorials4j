package tutorials4j.toolkit.cache.redis.autoconfigure;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@Getter
@Setter
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_CACHE_REDIS)
public class RedisCacheToolkitProperties extends NamedCacheOptions {
  private String templateKeyPrefix = "template:";
  private boolean enableStatistics = false;
  private String separator = "-";
  private Map<String, NamedCacheOptions> caches = new HashMap<>();
}
