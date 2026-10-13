package tutorials4j.toolkit.cache.redis.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * TODO
 *
 * @author YunJiao Yang
 */
@RequiredArgsConstructor
public class ToolkitKeySerializer extends StringRedisSerializer {
  private final String keyPrefix;

  @Override
  public byte[] serialize(String value) throws SerializationException {
    return super.serialize(keyPrefix + value);
  }
}
