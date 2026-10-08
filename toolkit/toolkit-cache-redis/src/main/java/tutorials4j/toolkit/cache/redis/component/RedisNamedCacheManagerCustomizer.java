package tutorials4j.toolkit.cache.redis.component;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@FunctionalInterface
public interface RedisNamedCacheManagerCustomizer {
  void customize(RedisNamedCacheManager redisNamedCacheManager);
}
