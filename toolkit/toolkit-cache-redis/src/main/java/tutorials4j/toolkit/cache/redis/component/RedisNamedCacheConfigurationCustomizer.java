package tutorials4j.toolkit.cache.redis.component;

import org.springframework.data.redis.cache.RedisCacheConfiguration;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface RedisNamedCacheConfigurationCustomizer {
  RedisCacheConfiguration customize(RedisCacheConfiguration redisCacheConfiguration);
}
