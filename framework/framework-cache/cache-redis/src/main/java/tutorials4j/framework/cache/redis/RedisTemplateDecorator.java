package tutorials4j.framework.cache.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 模板装饰器。
 *
 * <p>以全局单例的形式持有 {@link StringRedisTemplate} 与 {@link RedisTemplate} 实例，提供静态访问入口， 并封装了基于字符串序列化器的 Key
 * 序列化工具方法。
 *
 * @author Yun Jiao
 */
@Setter
@Getter
public class RedisTemplateDecorator {
  /** 全局单例实例。 */
  public static final RedisTemplateDecorator instance = new RedisTemplateDecorator();

  private StringRedisTemplate stringRedisTemplate;
  private RedisTemplate<Object, Object> redisTemplate;

  /**
   * 获取全局 {@link StringRedisTemplate} 实例。
   *
   * @return 字符串 Redis 模板实例
   */
  public static StringRedisTemplate stringRedisTemplate() {
    return instance.getStringRedisTemplate();
  }

  /**
   * 获取全局 {@link RedisTemplate} 实例。
   *
   * @return 对象 Redis 模板实例
   */
  public static RedisTemplate<Object, Object> redisTemplate() {
    return instance.getRedisTemplate();
  }

  /**
   * 使用字符串序列化器将 Key 序列化为字节数组。
   *
   * @param key 待序列化的 Key
   * @return 序列化后的字节数组
   */
  public byte[] serializeKey(String key) {
    return getStringRedisSerializer().serialize(key);
  }

  /**
   * 获取当前 {@link StringRedisTemplate} 使用的字符串 Key 序列化器。
   *
   * @return {@link StringRedisSerializer} 实例
   * @throws IllegalStateException 当 Key 序列化器不是 {@link StringRedisSerializer} 时抛出
   */
  private StringRedisSerializer getStringRedisSerializer() {
    if (stringRedisTemplate.getKeySerializer()
        instanceof StringRedisSerializer stringRedisSerializer) {
      return stringRedisSerializer;
    }

    throw new IllegalStateException("配置错误");
  }
}
