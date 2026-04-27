package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;

import java.util.List;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class RedisCacheManagerCreator implements Supplier<RedisCacheManager> {
    private final CacheRedisProperties properties;
    private final RedisConnectionFactory factory;
    private final List<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizer;
    private final List<CacheManagerCustomizer<RedisCacheManager>> cacheManagerCustomizer;

    private RedisCacheManager instance;

    @Override
    public RedisCacheManager get() {
        if (instance != null) {
            return instance;
        }

        synchronized (this) {
            if (instance != null) {
                return instance;
            }

            RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
            // 使用配置默认值
            defaultCacheConfig = RedisUtils.fillConfiguration(defaultCacheConfig, properties);

            RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory).cacheDefaults(defaultCacheConfig);
            if (properties.isEnableStatistics()) {
                builder.enableStatistics();
            }
            redisCacheManagerBuilderCustomizer.forEach(customizer -> customizer.customize(builder));

            RedisCacheManager redisCacheManager = builder.build();
            cacheManagerCustomizer.forEach(customizer -> customizer.customize(redisCacheManager));

            instance = redisCacheManager;
        }

        return instance;
    }
}
