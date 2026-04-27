package tutorials4j.framework.tenant.cache;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class TenantCacheConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Tenant |- Tenant Cache Configuration");
    }


    @Bean
    @ConditionalOnMissingBean
    TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator(CaffeineCacheManagerCreator caffeineCacheManagerCreator) {
        log.debug("Tutorials4j - Cache |- Tenant Caffeine Cache Manager Creator");

        return new TenantCaffeineCacheManagerCreator(caffeineCacheManagerCreator);
    }

    @Bean
    @ConditionalOnMissingBean
    TenantMultiLevelCacheManagerCreator tenantMultiLevelCacheManagerCreator(TenantCaffeineCacheManagerCreator tenantCaffeineCacheManagerCreator,
                                                                            RedisCacheManagerCreator redisCacheManagerCreator) {
        log.debug("Tutorials4j - Cache |- Tenant Multi Level Cache Manager Creator");

        return new TenantMultiLevelCacheManagerCreator(tenantCaffeineCacheManagerCreator, redisCacheManagerCreator);
    }

}
