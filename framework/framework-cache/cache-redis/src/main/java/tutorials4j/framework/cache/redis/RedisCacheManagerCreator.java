package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public class RedisCacheManagerCreator implements Supplier<RedisCacheManager> {
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

            RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
            RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory).cacheDefaults(configuration);
            redisCacheManagerBuilderCustomizer.forEach(customizer -> customizer.customize(builder));

            RedisCacheManager redisCacheManager = builder.build();
            cacheManagerCustomizer.forEach(customizer -> customizer.customize(redisCacheManager));

            instance = redisCacheManager;
        }

        return instance;
    }
}
