package tutorials4j.toolkit.cache.redis.autoconfigure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tutorials4j.toolkit.cache.redis.component.RedisCacheManagerCreator;
import tutorials4j.toolkit.cache.redis.component.RedisNamedCacheConfigurationCustomizer;
import tutorials4j.toolkit.cache.redis.component.RedisNamedCacheManagerCustomizer;
import tutorials4j.toolkit.json.component.JsonTypeWhiteListLoader;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({
  RedisCacheToolkitProperties.class,
})
public class RedisCacheToolkitAutoConfiguration {
  @PostConstruct
  public void postConstruct() {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Cache Toolkit Auto Configuration");
  }

  @Bean
  RedisCacheManagerCreator redisCacheManagerCreator(
      RedisCacheToolkitProperties properties,
      RedisConnectionFactory factory,
      ObjectProvider<RedisNamedCacheConfigurationCustomizer>
          redisNamedCacheConfigurationCustomizers,
      ObjectProvider<RedisNamedCacheManagerCustomizer> redisNamedCacheManagerCustomizers,
      GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer) {
    log.trace("[TOOLKIT-CACHE-REDIS] Redis Cache Manager Creator");
    return new RedisCacheManagerCreator(
        properties,
        factory,
        redisNamedCacheConfigurationCustomizers.orderedStream().toList(),
        redisNamedCacheManagerCustomizers.orderedStream().toList(),
        genericJacksonJsonRedisSerializer);
  }

  @Bean
  public GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer(
      JsonTypeWhiteListLoader jsonTypeWhiteListLoader) {
    PolymorphicTypeValidator typeValidator =
        BasicPolymorphicTypeValidator.builder()
            .allowIfSubType((ctx, clazz) -> jsonTypeWhiteListLoader.contains(clazz.getName()))
            .build();

    log.trace("[TOOLKIT-CACHE-REDIS] Generic Jackson Json Redis Serializer");
    return GenericJacksonJsonRedisSerializer.builder()
        .enableDefaultTyping(typeValidator)
        .typePropertyName("@class")
        .build();
  }
}
