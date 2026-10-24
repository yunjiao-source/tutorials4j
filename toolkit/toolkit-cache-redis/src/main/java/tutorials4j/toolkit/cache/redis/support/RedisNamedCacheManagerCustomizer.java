package tutorials4j.toolkit.cache.redis.support;

import tutorials4j.toolkit.cache.redis.component.RedisNamedCacheManager;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@FunctionalInterface
public interface RedisNamedCacheManagerCustomizer {
  void customize(RedisNamedCacheManager redisNamedCacheManager);
}
