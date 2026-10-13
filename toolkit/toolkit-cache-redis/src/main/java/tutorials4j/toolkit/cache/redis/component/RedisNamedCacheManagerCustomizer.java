package tutorials4j.toolkit.cache.redis.component;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@FunctionalInterface
public interface RedisNamedCacheManagerCustomizer {
  void customize(RedisNamedCacheManager redisNamedCacheManager);
}
