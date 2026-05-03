package tutorials4j.framework.examples.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantCaffeineCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantMultiLevelCacheManagerCreator;

/**
 * 两级缓存配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("cache")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.cache"})
public class TenantCacheableConfig implements CachingConfigurer {
    @Autowired
    private RedisCacheManagerCreator redisCacheManagerCreator;
    @Autowired
    private TenantMultiLevelCacheManagerCreator tenantMultiLevelCacheManagerCreator;
    @Autowired
    private TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator;
    @Autowired
    private CaffeineCacheManagerCreator caffeineCacheManagerCreator; // 这个不支持租户


    @Bean
    @Override
    public CacheManager cacheManager() {
        //return redisCacheManagerCreator.getInstance();
        return tenantMultiLevelCacheManagerCreator.getInstance();
        //return tenantCaffeineCacheManagerCreator.getInstance();
        //return caffeineCacheManagerCreator.getInstance();
    }
}
