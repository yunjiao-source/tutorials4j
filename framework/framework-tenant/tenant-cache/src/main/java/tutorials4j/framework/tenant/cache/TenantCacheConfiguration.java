package tutorials4j.framework.tenant.cache;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;

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
    TenantCaffeineCacheManager tenantCaffeineCacheManager(CaffeineCacheManagerCreator CaffeineCacheManagerCreator) {
        log.debug("Tutorials4j - Cache |- Tenant Caffeine CacheManager");

        return new TenantCaffeineCacheManager(CaffeineCacheManagerCreator);
    }

}
