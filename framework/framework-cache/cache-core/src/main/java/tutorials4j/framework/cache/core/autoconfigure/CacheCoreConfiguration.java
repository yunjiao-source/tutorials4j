package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.CacheManagerSupplier;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 缓存核心配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CacheRedisProperties.class)
public class CacheCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Cache Core Configuration");
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    CompositeCacheManager compositeCacheManager(ObjectProvider<CacheManagerSupplier> cacheManagerSuppliers) {
        log.debug("Tutorials4j |- Composite Cache Manager");
        List<CacheManager> cacheManagers = cacheManagerSuppliers
                .orderedStream()
                .map(CacheManagerSupplier::get)
                .collect(Collectors.toList());
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setCacheManagers(cacheManagers);
        return compositeCacheManager;
    }
}
