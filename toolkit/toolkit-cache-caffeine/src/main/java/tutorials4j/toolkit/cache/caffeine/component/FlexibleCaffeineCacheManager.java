package tutorials4j.toolkit.cache.caffeine.component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.toolkit.cache.autoconfigure.NamedCacheOptions;
import tutorials4j.toolkit.cache.autoconfigure.NamedCacheToolkitProperties;
import tutorials4j.toolkit.cache.exception.NamedCacheMissingConfigException;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager {
  private final NamedCacheToolkitProperties properties;

  public FlexibleCaffeineCacheManager(NamedCacheToolkitProperties properties) {
    this.properties = properties;
    this.setAllowNullValues(properties.isCacheNullValues());
  }

  public FlexibleCaffeineCacheManager(
      NamedCacheToolkitProperties properties, String... cacheNames) {
    super(cacheNames);
    this.properties = properties;
    this.setAllowNullValues(properties.isCacheNullValues());
  }

  @Override
  protected Cache<Object, Object> createNativeCaffeineCache(String name) {
    Map<String, NamedCacheOptions> optionsMap = properties.getCaches();

    // 获取独立配置
    NamedCacheOptions options = optionsMap.get(name);
    if (options == null) {
      throw new NamedCacheMissingConfigException(name);
    }

    Caffeine<Object, Object> caffeine =
        Caffeine.newBuilder()
            .initialCapacity(options.getInitialCapacity())
            .maximumSize(options.getMaximumSize())
            .expireAfterWrite(options.getTimeToLive());

    if (properties.isEnableStatistics()) {
      caffeine.recordStats();
    }

    if (log.isDebugEnabled()) {
      log.debug("Caffeine 缓存初始化, name={}, options={}", name, options);
    }
    return caffeine.build();
  }
}
