package tutorials4j.framework.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import tutorials4j.framework.cache.core.properties.CacheRedisProperties;
import tutorials4j.framework.cache.core.support.CacheManagerCreatorCategory;
import tutorials4j.framework.cache.redis.ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.NamedCacheManagerCustomizer;
import tutorials4j.framework.cache.redis.NamedRedisCacheManagerBuilderCustomizer;
import tutorials4j.framework.cache.redis.RedisCacheManagerCreator;

import java.util.stream.Collectors;

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
        log.debug("[CACHE-REDIS] Cache Redis Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer jsonSerializerRedisCacheManagerBuilderCustomizer() {
        log.debug("[CACHE-REDIS] Json Serializer Value Redis Cache Manager Builder Customizerr");
        return new ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    NamedRedisCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(CacheRedisProperties properties) {
        log.debug("[CACHE-REDIS] Named Redis Cache Manager Builder Customizer");
        return new NamedRedisCacheManagerBuilderCustomizer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
        log.debug("[CACHE-REDIS] Named Redis Cache Manager Customizer");
        return new NamedCacheManagerCustomizer();
    }

    @Bean(CacheManagerCreatorCategory.REDIS_CREATOR)
    @ConditionalOnMissingBean
    RedisCacheManagerCreator redisCacheManagerCreator(CacheRedisProperties properties,
                                                      RedisConnectionFactory factory,
                                                      ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                                      ObjectProvider<CacheManagerCustomizer<RedisCacheManager>> cacheManagerCustomizers) {
        log.debug("[CACHE-REDIS] Redis Cache Manager Creator");
        return new RedisCacheManagerCreator(properties,factory,
                redisCacheManagerBuilderCustomizers.orderedStream().collect(Collectors.toList()),
                cacheManagerCustomizers.orderedStream().collect(Collectors.toList()));
    }
}
