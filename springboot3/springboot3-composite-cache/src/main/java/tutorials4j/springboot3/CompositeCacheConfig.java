package tutorials4j.springboot3;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import tutorials4j.framework.cache.redis.RedisUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 组合缓存配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@RequiredArgsConstructor
public class CompositeCacheConfig implements CachingConfigurer {
    private final RedisConnectionFactory factory;

    @Override
    public CacheManager cacheManager() {
        CompositeCacheManager cacheManager = new CompositeCacheManager();
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("users");
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(10, TimeUnit.SECONDS));

        RedisCacheConfiguration redisCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(RedisUtils.tutorials4jCacheKeyPrefix());
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("orders", redisCacheConfig.entryTtl(Duration.ofSeconds(10)));

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(redisCacheConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
        /**
         * 这里需要注意：要configMap配置生效，必须调用afterPropertiesSet()方法，方法会创建所有缓存；
         * 如果不调用，会在运行期创建，创建时会使用默认配置，非configMap中的配置
         */
        redisCacheManager.afterPropertiesSet();

        // 查看配置是否生效
        redisCacheManager.getCache("orders").getNativeCache(); // 触发初始化
        Map<String, RedisCacheConfiguration> conf = redisCacheManager.getCacheConfigurations();

        cacheManager.setCacheManagers(Arrays.asList(caffeineCacheManager, redisCacheManager));
        return cacheManager;
    }

}
