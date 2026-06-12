package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tutorials4j.framework.cache.core.RedisKeyPrefix;
import tutorials4j.framework.cache.redis.util.RedisUtils;

/**
 * 在序列化字符串 Key 时自动添加全局默认前缀的 Redis Key 序列化器。
 *
 * <p>该序列化器继承自 {@link StringRedisSerializer}，重写了 {@code serialize} 方法， 在序列化前通过 {@link
 * RedisUtils#tenantCacheKeyPrefix()} 计算出带租户、缓存名前缀的完整 Key， 然后交给父类完成字符串到字节数组的转换。
 *
 * @author Yun Jiao
 * @see StringRedisSerializer
 * @see RedisUtils#tenantCacheKeyPrefix()
 */
@RequiredArgsConstructor
public class TenantKeySerializer extends StringRedisSerializer {
  private final String cacheName;

  @Override
  public byte[] serialize(String value) throws SerializationException {
    return super.serialize(RedisKeyPrefix.tenant().compute(cacheName) + value);
  }
}
