package tutorials4j.toolkit.cache.caffeine.autoconfigure;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Getter
@Setter
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_CACHE_CAFFEINE)
public class CaffeineCacheToolkitProperties extends NamedCacheOptions {
  private boolean cacheNullValues = true;
  private Map<String, NamedCacheOptions> caches = new HashMap<>();
}
