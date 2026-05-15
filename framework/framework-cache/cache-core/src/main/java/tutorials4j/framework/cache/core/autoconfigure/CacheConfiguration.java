package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.lock.LockService;
import tutorials4j.framework.cache.core.lock.LockServiceFactory;
import tutorials4j.framework.cache.core.properties.CacheCoreProperties;
import tutorials4j.framework.cache.core.properties.NamedCacheProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;

import java.util.List;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({CacheCoreProperties.class, NamedCacheProperties.class,})
public class CacheConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[CACHE-CORE] Cache Core Configuration");
    }


    @Bean
    CacheManagerCreatorFactory cacheManagerCreatorFactory(List<CacheManagerCreator<?>> cacheManagerCreators) {
        log.debug("[CACHE-CORE] Cache Manager Creator Factory");
        CacheManagerCreatorFactory factory = new CacheManagerCreatorFactory();
        factory.setCacheManagerCreators(cacheManagerCreators);

        log.debug("[CACHE-CORE] 工厂'CacheManagerCreatorFactory'注入实例：{}", cacheManagerCreators);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    LockServiceFactory lockServiceFactory(List<LockService> lockServices) {
        log.debug("[CACHE-CORE] Lock Service Factory");

        LockServiceFactory factory = new LockServiceFactory();
        factory.setDistributedLockService(lockServices);

        log.debug("[CACHE-CORE] 工厂'LockServiceFactory'注入实例：{}", lockServices);
        return factory;
    }
}
