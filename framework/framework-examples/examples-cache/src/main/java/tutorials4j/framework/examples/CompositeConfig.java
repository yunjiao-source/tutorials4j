package tutorials4j.framework.examples;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import tutorials4j.framework.cache.core.support.CacheManagerSupplier;
import tutorials4j.framework.cache.core.support.CompositeCacheManagerCreator;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 组合缓存应用配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("composite")
public class CompositeConfig {

    @Bean
    @Order(1)
    CacheManagerSupplier caffineCache() {
        return () -> {
            CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("users");
            caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                    .initialCapacity(100)
                    .maximumSize(500)
                    .expireAfterAccess(10, TimeUnit.SECONDS));
            return caffeineCacheManager;
        };
    }

    @Bean
    @Order(2)
    CacheManagerSupplier redisCache(RedisConnectionFactory factory,
                                    ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                    CacheManagerCustomizers cacheManagerCustomizers) {
        return () -> {
            // 默认配置
            RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(1))
                    .prefixCacheNameWith("share");
            RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory).cacheDefaults(configuration);

            redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
            return cacheManagerCustomizers.customize(builder.build());
        };
    }

    @Configuration
    @RequiredArgsConstructor
    class CompositeCachingConfigurer implements CachingConfigurer {
        private final CompositeCacheManagerCreator compositeCacheManagerCreator;

        @Override
        public CacheManager cacheManager() {
            return compositeCacheManagerCreator.get();
        }
    }
}
