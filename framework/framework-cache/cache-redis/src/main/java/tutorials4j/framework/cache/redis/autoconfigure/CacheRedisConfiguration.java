package tutorials4j.framework.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.redis.NamedCacheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.NamedCacheManagerCustomizer;

/**
 * 命名缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CacheRedisConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j |- Cache Redis Configuration");
    }


    @Configuration(proxyBeanMethods = false)
    public static class NamedCacheConfiguration {
        @Bean
        NamedCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(CacheRedisProperties properties) {
            log.debug("Tutorials4j |- Named Redis Cache Manager Builder Customizer");
            return new NamedCacheManagerBuilderCustomizer(properties);
        }

        @Bean
        NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
            log.debug("Tutorials4j |- Named Redis Cache Manager Customizer");
            return new NamedCacheManagerCustomizer();
        }
    }
}
