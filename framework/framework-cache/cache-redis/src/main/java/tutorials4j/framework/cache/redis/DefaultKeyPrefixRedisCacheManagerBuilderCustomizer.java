package tutorials4j.framework.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class DefaultKeyPrefixRedisCacheManagerBuilderCustomizer implements RedisCacheManagerBuilderCustomizer {
    @Override
    public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
        // 获取默认配置
        RedisCacheConfiguration defaultConfiguration = builder.cacheDefaults();
        CacheKeyPrefix cacheKeyPrefix = defaultConfiguration.getKeyPrefix();

        String oldKeyPrefix = RedisUtils.parseCacheNamePrefix(cacheKeyPrefix);
        builder.cacheDefaults(defaultConfiguration.computePrefixWith(RedisUtils.defaultCacheKeyPrefix(oldKeyPrefix)));
    }
}
