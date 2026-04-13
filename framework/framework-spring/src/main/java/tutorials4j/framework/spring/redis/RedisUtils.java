package tutorials4j.framework.spring.redis;

import org.springframework.data.redis.cache.CacheKeyPrefix;

/**
 * 工具
 *
 * @author Yun Jiao
 */
public interface RedisUtils {

    static CacheKeyPrefix tutorials4jCacheKeyPrefix() {
        return CacheKeyPrefix.prefixed("tutorials4j:");
    }
}
