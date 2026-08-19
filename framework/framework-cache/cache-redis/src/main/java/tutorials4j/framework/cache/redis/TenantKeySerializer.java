package tutorials4j.framework.cache.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tutorials4j.framework.cache.core.CacheNamePrefix;

/**
 * 在序列化字符串 Key 时自动添加全局默认前缀的 Redis Key 序列化器。
 *
 * <p>该序列化器继承自 {@link StringRedisSerializer}，重写了 {@code serialize} 方法， 在序列化前通过 {@link
 * CacheNamePrefix#tenant()} 计算出带租户、缓存名前缀的完整 Key， 然后交给父类完成字符串到字节数组的转换。
 *
 * @author Yun Jiao
 * @see StringRedisSerializer
 * @see CacheNamePrefix#tenant()
 */
@RequiredArgsConstructor
public class TenantKeySerializer extends StringRedisSerializer {
  /** 缓存名称，用于计算带租户的 Key 前缀。 */
  private final String cacheName;

  /**
   * 序列化字符串 Key，自动拼接租户与缓存名前缀后交给父类完成序列化。
   *
   * @param value 原始 Key 字符串
   * @return 序列化后的字节数组
   * @throws SerializationException 序列化失败时抛出
   */
  @Override
  public byte[] serialize(String value) throws SerializationException {
    return super.serialize(CacheNamePrefix.tenant().end().compute(cacheName) + value);
  }
}
