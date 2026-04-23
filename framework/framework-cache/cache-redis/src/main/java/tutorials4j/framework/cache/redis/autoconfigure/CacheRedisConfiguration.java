package tutorials4j.framework.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.redis.DefaultKeyPrefixRedisCacheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.NamedCacheManagerCustomizer;
import tutorials4j.framework.cache.redis.NamedRedisCacheManagerBuilderCustomizer;

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
        log.debug("Tutorials4j - Cache - Redis |- Cache Redis Configuration");
    }


    @Bean
    @Order(1)
    DefaultKeyPrefixRedisCacheManagerBuilderCustomizer defaultKeyPrefixRedisCacheManagerBuilderCustomizer() {
        log.debug("Tutorials4j - Cache - Redis |- Default Key Prefix Redis Cache Manager Builder Customizer");
        return new DefaultKeyPrefixRedisCacheManagerBuilderCustomizer();
    }

    @Configuration(proxyBeanMethods = false)
    public static class NamedCacheConfiguration {


        @Bean
        @Order
        NamedRedisCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(CacheRedisProperties properties) {
            log.debug("Tutorials4j - Cache - Redis |- Named Redis Cache Manager Builder Customizer");
            return new NamedRedisCacheManagerBuilderCustomizer(properties);
        }

        @Bean
        NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
            log.debug("Tutorials4j - Cache - Redis |- Named Redis Cache Manager Customizer");
            return new NamedCacheManagerCustomizer();
        }
    }
}
