package tutorials4j.toolkit.cache.redis.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

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
}
