package tutorials4j.toolkit.cache.caffeine.component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.toolkit.cache.NamedCacheMissingException;
import tutorials4j.toolkit.cache.caffeine.autoconfigure.CaffeineCacheToolkitProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class CaffeineNamedCacheManager extends CaffeineCacheManager {
  private final CaffeineCacheToolkitProperties properties;

  public CaffeineNamedCacheManager(CaffeineCacheToolkitProperties properties) {
    this.properties = properties;
    this.setAllowNullValues(properties.isCacheNullValues());
  }

  public CaffeineNamedCacheManager(
      CaffeineCacheToolkitProperties properties, String... cacheNames) {
    super(cacheNames);
    this.properties = properties;
    this.setAllowNullValues(properties.isCacheNullValues());
  }

  @Override
  protected Cache<Object, Object> createNativeCaffeineCache(String name) {
    var optionsMap = properties.getCaches();

    // 获取独立配置
    var options = optionsMap.get(name);
    if (options == null) {
      throw new NamedCacheMissingException(name);
    }

    var caffeine =
        Caffeine.newBuilder()
            .maximumSize(options.getMaximumSize())
            .expireAfterWrite(options.getExpireAfterWrite());
    if (options.getInitialCapacity() != null) {
      caffeine.initialCapacity(options.getInitialCapacity());
    }
    if (options.getExpireAfterAccess() != null) {
      caffeine.expireAfterAccess(options.getExpireAfterAccess());
    }
    if (options.isRecordStats()) {
      caffeine.recordStats();
    }

    if (log.isDebugEnabled()) {
      log.debug("Caffeine 缓存初始化, name={}, options={}", name, options);
    }
    return caffeine.build();
  }
}
