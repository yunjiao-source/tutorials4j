package tutorials4j.framework.cache.redis.customizer;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * 值Json序列化
 *
 * @author Yun Jiao
 */
public class ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer
    implements RedisCacheManagerBuilderCustomizer {
  @Override
  public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
    RedisCacheConfiguration defaultConfig = builder.cacheDefaults();
    defaultConfig =
        defaultConfig.serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));
    builder.cacheDefaults(defaultConfig);
  }
}
