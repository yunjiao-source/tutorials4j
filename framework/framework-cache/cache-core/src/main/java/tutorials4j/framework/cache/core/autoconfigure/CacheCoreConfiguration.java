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

/**
 * 缓存核心配置类。
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

    /**
     * 创建组合缓存管理器创建器 Bean。
     *
     * <p>该 Bean 会在容器中不存在自定义 {@link CompositeCacheManagerCreator} 时被创建。
     * 它通过 {@link ObjectProvider} 注入所有 {@link CacheManagerSupplier} 实例，
     * 用于后续构造 {@link org.springframework.cache.support.CompositeCacheManager}。
     *
     * @param cacheManagerSuppliers 容器中提供的所有缓存管理器供应商
     * @return 组合缓存管理器创建器实例
     */
    @Bean
    @ConditionalOnMissingBean
    CompositeCacheManagerCreator compositeCacheManagerCreator(ObjectProvider<CacheManagerSupplier> cacheManagerSuppliers) {
        log.debug("Tutorials4j - Cache |- Composite Cache Manager Creator");

        return new CompositeCacheManagerCreator(cacheManagerSuppliers);
    }
}
