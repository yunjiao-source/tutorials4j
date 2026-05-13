package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        return factory;
    }
}
