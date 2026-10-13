package tutorials4j.toolkit.cache.caffeine.component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import tutorials4j.toolkit.cache.CacheErrorCode;
import tutorials4j.toolkit.cache.caffeine.autoconfigure.CaffeineCacheToolkitProperties;

/**
 * TODO
 *
 * @author YunJiao Yang
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
    this.properties = properties;
    this.setAllowNullValues(properties.isCacheNullValues());
    // 必须在 properties 赋值之后调用：setCacheNames 会立即创建其中所有的命名缓存
    this.setCacheNames(Arrays.asList(cacheNames));
  }

  @Override
  protected Cache<Object, Object> createNativeCaffeineCache(String name) {
    var optionsMap = properties.getCaches();

    // 获取独立配置
    var options = optionsMap.get(name);
    if (options == null) {
      throw CacheErrorCode.NAMED_CACHE_NOT_CONFIG.throwed().param("name", name);
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
