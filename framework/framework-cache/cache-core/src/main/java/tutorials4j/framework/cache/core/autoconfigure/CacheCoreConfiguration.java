package tutorials4j.framework.cache.core.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.core.support.CacheManagerSupplier;
import tutorials4j.framework.cache.core.support.CompositeCacheManagerCreator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 缓存核心配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({CacheRedisProperties.class})
public class CacheCoreConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Cache |- Cache Core Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    CompositeCacheManagerCreator compositeCacheManagerCreator(ObjectProvider<CacheManagerSupplier> cacheManagerSuppliers) {
        log.debug("Tutorials4j - Cache |- Composite Cache Manager Creator");
        List<CacheManagerSupplier> cacheManagers = cacheManagerSuppliers
                .orderedStream()
                .collect(Collectors.toList());
        return new CompositeCacheManagerCreator(cacheManagers);
    }
}
