package tutorials4j.framework.cache.redis.customizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Redis 缓存管理器构建器定制器：将缓存值的序列化方式配置为基于 Jackson 的 JSON 序列化。
 *
 * <p>该定制器使用传入的 {@link ObjectMapper} 构建 {@link GenericJackson2JsonRedisSerializer}， 替换 {@link
 * RedisCacheManager.RedisCacheManagerBuilder} 默认缓存配置中的值序列化器。
 *
 * @author Yun Jiao
 * @see RedisCacheManagerBuilderCustomizer
 * @see GenericJackson2JsonRedisSerializer
 */
@RequiredArgsConstructor
public class ValueJsonSerializerRedisCacVaheManagerBuilderCustomizer
    implements RedisCacheManagerBuilderCustomizer {
  /** 用于构造 JSON 序列化器的 Jackson 对象映射器。 */
  private final ObjectMapper objectMapper;

  /**
   * 定制缓存管理器构建器：将默认缓存配置的值序列化器替换为 JSON 序列化器。
   *
   * @param builder 要定制的 {@link RedisCacheManager.RedisCacheManagerBuilder} 实例
   */
  @Override
  public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
    RedisCacheConfiguration defaultConfig = builder.cacheDefaults();
    defaultConfig =
        defaultConfig.serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(objectMapper)));
    builder.cacheDefaults(defaultConfig);
  }
}
