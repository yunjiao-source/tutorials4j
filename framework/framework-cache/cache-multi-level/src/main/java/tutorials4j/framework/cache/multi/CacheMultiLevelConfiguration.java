package tutorials4j.framework.cache.multi;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.caffeine.CaffeineCacheManagerCreator;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

/**
 * Spring 配置类，用于启用多级缓存（本地 Caffeine + 远程 Redis）支持。
 *
 * @author Yun Jiao
 * @see MultiLevelCacheManagerCreator
 * @see MultiLevelCacheManager
 * @see MultiLevelCache
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CacheMultiLevelConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Cache |- Cache Multi Level Configuration");
    }

    @Bean
    @ConditionalOnMissingBean(MultiLevelCacheManagerCreator.class)
    MultiLevelCacheManagerCreator multiLevelCacheManagerCreator(CaffeineCacheManagerCreator caffeineCacheManagerCreator,
                                                                RedisCacheManagerCreator redisCacheManagerCreator) {
        log.debug("Tutorials4j - Cache |- Multi Level Cache Manager Creator");

        return new MultiLevelCacheManagerCreator(caffeineCacheManagerCreator, redisCacheManagerCreator);
    }

}
