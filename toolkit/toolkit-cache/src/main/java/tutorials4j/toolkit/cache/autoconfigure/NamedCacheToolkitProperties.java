package tutorials4j.toolkit.cache.autoconfigure;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tutorials4j.toolkit.core.constant.PropertyConsts;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Data
@ConfigurationProperties(prefix = PropertyConsts.PROPERTY_TOOLKIT_NAMED_CACHE)
public class NamedCacheToolkitProperties {
  private String cacheNamePrefix = "named-cache:";
  private boolean cacheNullValues;
  private boolean enableStatistics;
  private Map<String, NamedCacheOptions> caches = new HashMap<>();
}
