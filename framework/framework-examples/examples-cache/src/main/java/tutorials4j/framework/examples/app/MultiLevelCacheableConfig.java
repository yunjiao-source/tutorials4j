package tutorials4j.framework.examples.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tutorials4j.framework.cache.multi.MultiLevelCacheManagerCreator;

/**
 * 两级缓存配置
 *
 * @author Yun Jiao
 */
@EnableCaching
@Configuration
@Profile("multi-level")
@ComponentScan(basePackages = {"tutorials4j.framework.examples.multi"})
public class MultiLevelCacheableConfig implements CachingConfigurer {
    @Autowired
    private MultiLevelCacheManagerCreator cacheManagerCreator;

    @Bean
    @Override
    public CacheManager cacheManager() {
        return cacheManagerCreator.get();
    }
}
