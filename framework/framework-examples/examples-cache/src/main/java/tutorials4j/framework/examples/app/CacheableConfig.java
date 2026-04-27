package tutorials4j.framework.examples.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * 组合缓存应用配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("cacheable")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cacheable"})
public class CacheableConfig implements CachingConfigurer {
    @Autowired
    private RedisCacheManagerCreator redisCacheManagerCreator;

    @Bean
    CaffeineCacheManager caffeineCacheManager(CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
        return caffeineCacheManagerCreator.get();
    }


    @Bean
    @Override
    public CacheManager cacheManager() {
        return redisCacheManagerCreator.get();
    }
}
