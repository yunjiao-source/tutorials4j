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
        log.debug("Tutorials4j - Cache |- Cache Redis Configuration");
    }

    @Bean
    @ConditionalOnMissingBean
    ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer jsonSerializerRedisCacheManagerBuilderCustomizer() {
        log.debug("Tutorials4j - Cache |- Json Serializer Value Redis Cache Manager Builder Customizerr");
        return new ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    NamedRedisCacheManagerBuilderCustomizer namedRedisCacheManagerBuilderCustomizer(CacheRedisProperties properties) {
        log.debug("Tutorials4j - Cache |- Named Redis Cache Manager Builder Customizer");
        return new NamedRedisCacheManagerBuilderCustomizer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    NamedCacheManagerCustomizer namedRedisCacheManagerCustomizer() {
        log.debug("Tutorials4j - Cache |- Named Redis Cache Manager Customizer");
        return new NamedCacheManagerCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    RedisCacheManagerCreator redisCacheManagerCreator(CacheRedisProperties properties,
                                                      RedisConnectionFactory factory,
                                                      ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
                                                      ObjectProvider<CacheManagerCustomizer<RedisCacheManager>> cacheManagerCustomizers) {
        log.debug("Tutorials4j - Cache |- Redis Cache Manager Creator");
        return new RedisCacheManagerCreator(properties,factory,
                redisCacheManagerBuilderCustomizers.orderedStream().collect(Collectors.toList()),
                cacheManagerCustomizers.orderedStream().collect(Collectors.toList()));
    }
}
