package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CacheCaffeineProperties;
import tutorials4j.framework.cache.core.properties.CacheCoreProperties;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreator;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorFactory;

import java.util.Map;

/**
 * 缓存核心配置类。
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({CacheCoreProperties.class,
        CacheRedisProperties.class,
        CacheCaffeineProperties.class})
public class CacheCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("[CACHE-CORE] Cache Core Configuration");
    }


    @Configuration(proxyBeanMethods = false)
    static class CoreUtilsConfiguration {
        @Autowired(required = false)
        private Map<String, CacheManagerCreator<?>> cacheManagerCreators;

        @PostConstruct
        public void injectCacheManagerCreators() {
            if (cacheManagerCreators != null && !cacheManagerCreators.isEmpty()) {
                CacheManagerCreatorFactory.INSTANCE.setCacheManagerCreators(cacheManagerCreators);
                log.debug("[CACHE-CORE] 工厂CacheManagerCreatorFactory成功注入实例：{}", cacheManagerCreators);
            } else {
                log.debug("[CACHE-CORE] 工厂CacheManagerCreatorCategory未注入实例");

            }
        }
    }
}
