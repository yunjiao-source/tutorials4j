package tutorials4j.framework.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheManager;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.redis.NamedCacheManagerCustomizer;
import tutorials4j.framework.cache.redis.NamedRedisCacheManagerBuilderCustomizer;

/**
 * 命名缓存配置
 *
 * @author Yun Jiao
 */
@Slf4j
@ConditionalOnClass(RedisCacheManager.class)
@Configuration(proxyBeanMethods = false)
public class CacheRedisConfiguration {
    @PostConstruct
    public void postConstruct() {
        log.debug("Tutorials4j - Cache - Redis |- Cache Redis Configuration");
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
