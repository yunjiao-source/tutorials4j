package tutorials4j.framework.cache.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Setter
@Getter
public class RedisTemplateDecorator {
  public static final RedisTemplateDecorator instance = new RedisTemplateDecorator();

  private StringRedisTemplate stringRedisTemplate;
  private RedisTemplate<Object, Object> redisTemplate;

  public static StringRedisTemplate stringRedisTemplate() {
    return instance.getStringRedisTemplate();
  }

  public static RedisTemplate<Object, Object> redisTemplate() {
    return instance.getRedisTemplate();
  }

  public byte[] serializeKey(String key) {
    return getStringRedisSerializer().serialize(key);
  }

  private StringRedisSerializer getStringRedisSerializer() {
    if (stringRedisTemplate.getKeySerializer()
        instanceof StringRedisSerializer stringRedisSerializer) {
      return stringRedisSerializer;
    }

    throw new IllegalStateException("配置错误");
  }
}
