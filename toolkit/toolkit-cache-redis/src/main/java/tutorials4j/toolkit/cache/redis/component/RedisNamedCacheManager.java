package tutorials4j.toolkit.cache.redis.component;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import tutorials4j.toolkit.cache.NamedCacheMissingException;
import tutorials4j.toolkit.cache.redis.autoconfigure.NamedCacheOptions;
import tutorials4j.toolkit.cache.redis.autoconfigure.RedisCacheToolkitProperties;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public class RedisNamedCacheManager extends RedisCacheManager {
  private final RedisCacheToolkitProperties properties;

  public RedisNamedCacheManager(
      RedisCacheToolkitProperties properties,
      RedisCacheWriter cacheWriter,
      RedisCacheConfiguration defaultCacheConfiguration) {
    super(cacheWriter, defaultCacheConfiguration);
    this.properties = properties;
  }

  public RedisNamedCacheManager(
      RedisCacheToolkitProperties properties,
      RedisCacheWriter cacheWriter,
      RedisCacheConfiguration defaultCacheConfiguration,
      String... initialCacheNames) {
    super(cacheWriter, defaultCacheConfiguration, initialCacheNames);
    this.properties = properties;
  }

  public RedisNamedCacheManager(
      RedisCacheToolkitProperties properties,
      RedisCacheWriter cacheWriter,
      RedisCacheConfiguration defaultCacheConfiguration,
      Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
    super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
    this.properties = properties;
  }

  @Override
  protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
    Map<String, NamedCacheOptions> caches = properties.getCaches();
    if (caches == null || !caches.containsKey(name)) {
      throw new NamedCacheMissingException(name);
    }

    NamedCacheOptions options = caches.get(name);
    cacheConfig = cacheConfig.entryTtl(options.getTimeToLive());
    if (!options.isCachingNullValues()) {
      cacheConfig = cacheConfig.disableCachingNullValues();
    }
    if (StringUtils.isNotBlank(options.getKeyPrefix())) {
      cacheConfig = cacheConfig.prefixCacheNameWith(options.getKeyPrefix());
    }
    return super.createRedisCache(name, cacheConfig);
  }
}
