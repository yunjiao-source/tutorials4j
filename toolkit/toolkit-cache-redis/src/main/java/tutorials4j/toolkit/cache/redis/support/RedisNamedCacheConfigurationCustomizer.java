package tutorials4j.toolkit.cache.redis.support;

import org.springframework.data.redis.cache.RedisCacheConfiguration;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@FunctionalInterface
public interface RedisNamedCacheConfigurationCustomizer {
  RedisCacheConfiguration customize(RedisCacheConfiguration redisCacheConfiguration);
}
