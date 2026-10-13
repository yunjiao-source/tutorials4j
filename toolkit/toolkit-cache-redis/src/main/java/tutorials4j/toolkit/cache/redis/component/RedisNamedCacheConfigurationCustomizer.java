package tutorials4j.toolkit.cache.redis.component;

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
