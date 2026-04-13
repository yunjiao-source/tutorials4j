package tutorials4j.framework.spring.redis;

import org.springframework.data.redis.cache.CacheKeyPrefix;

/**
 * 工具
 *
 * @author Yun Jiao
 */
public interface RedisUtils {
    String PREFIX = "tutorials4j:cache:";
    static CacheKeyPrefix cacheKeyPrefix() {
        return CacheKeyPrefix.prefixed(PREFIX);
    }

    static CacheKeyPrefix cacheKeyPrefix(String name) {
        return CacheKeyPrefix.prefixed(PREFIX + name + ":");
    }
}
