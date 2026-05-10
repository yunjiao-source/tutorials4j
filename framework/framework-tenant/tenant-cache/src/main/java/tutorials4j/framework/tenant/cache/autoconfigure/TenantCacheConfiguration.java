package tutorials4j.framework.tenant.cache.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantCaffeineCacheManagerCreator;
import tutorials4j.framework.tenant.cache.TenantMultiLevelCacheManagerCreator;

/**
 * 租户缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class TenantCacheConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[TENANT-CACHE] Cache Configuration");
    }


    @Bean
    @ConditionalOnMissingBean
    TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator(CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
        log.debug("[TENANT-CACHE] Tenant Caffeine Cache Manager Creator");

        return new TenantCaffeineCacheManagerCreator(caffeineCacheManagerCreator);
    }

    @Bean
    @ConditionalOnMissingBean
    TenantMultiLevelCacheManagerCreator tenantMultiLevelCacheManagerCreator(TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator,
                                                                            RedisCacheManagerCreator redisCacheManagerCreator) {
        log.debug("[TENANT-CACHE] Tenant Multi Level Cache Manager Creator");

        return new TenantMultiLevelCacheManagerCreator(tenantCaffeineCacheManagerCreator, redisCacheManagerCreator);
    }

}
