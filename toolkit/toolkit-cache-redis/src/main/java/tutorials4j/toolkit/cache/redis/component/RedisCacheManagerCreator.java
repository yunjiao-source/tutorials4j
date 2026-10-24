package tutorials4j.toolkit.cache.redis.component;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tutorials4j.toolkit.cache.CacheManagerCreator;
import tutorials4j.toolkit.cache.CacheType;
import tutorials4j.toolkit.cache.redis.autoconfigure.RedisCacheToolkitProperties;
import tutorials4j.toolkit.cache.redis.support.RedisNamedCacheConfigurationCustomizer;
import tutorials4j.toolkit.cache.redis.support.RedisNamedCacheManagerCustomizer;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public class RedisCacheManagerCreator implements CacheManagerCreator<RedisNamedCacheManager> {
  private final RedisCacheToolkitProperties properties;
  private final RedisConnectionFactory redisConnectionFactory;
  private final List<RedisNamedCacheConfigurationCustomizer>
      redisNamedCacheConfigurationCustomizers;
  private final List<RedisNamedCacheManagerCustomizer> redisNamedCacheManagerCustomizers;
  private final GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer;
  private RedisNamedCacheManager instance;

  @Override
  public CacheType getCacheType() {
    return CacheType.redis;
  }

  @Override
  public RedisNamedCacheManager getInstance() {
    if (instance != null) {
      return instance;
    }

    synchronized (this) {
      if (instance != null) {
        return instance;
      }

      instance = newInstance();
    }

    return instance;
  }

  @Override
  public RedisNamedCacheManager newInstance() {
    var enableStatistics = properties.isEnableStatistics();
    var redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
    if (enableStatistics) {
      redisCacheWriter =
          redisCacheWriter.withStatisticsCollector(CacheStatisticsCollector.create());
    }

    var redisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(properties.getTimeToLive())
            // 默认使用Json序列化值
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    genericJacksonJsonRedisSerializer));
    var allowNullValues = properties.isCachingNullValues();
    if (!allowNullValues) {
      redisCacheConfiguration = redisCacheConfiguration.disableCachingNullValues();
    }
    // 自定义配置
    for (RedisNamedCacheConfigurationCustomizer redisNamedCacheConfigurationCustomizer :
        redisNamedCacheConfigurationCustomizers) {
      redisCacheConfiguration =
          redisNamedCacheConfigurationCustomizer.customize(redisCacheConfiguration);
    }

    var redisNamedCacheManager =
        new RedisNamedCacheManager(properties, redisCacheWriter, redisCacheConfiguration);
    redisNamedCacheManager.setTransactionAware(false);
    // 自定义配置
    redisNamedCacheManagerCustomizers.forEach(e -> e.customize(redisNamedCacheManager));

    redisNamedCacheManager.afterPropertiesSet();
    return redisNamedCacheManager;
  }

  @Override
  public Class<RedisNamedCacheManager> getBeanClass() {
    return RedisNamedCacheManager.class;
  }
}
