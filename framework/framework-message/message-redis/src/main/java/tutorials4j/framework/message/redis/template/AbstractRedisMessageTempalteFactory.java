package tutorials4j.framework.message.redis.template;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.properties.QueueOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequiredArgsConstructor
public abstract class AbstractRedisMessageTempalteFactory<T>
    implements RedisMessageTemplateFactory<T> {
  @Setter private StringRedisTemplate stringRedisTemplate;
  @Setter private JacksonRecord jacksonRecord;
  @Setter private Map<String, QueueOptions> queueOptionsMap;

  private final Map<String, T> templateMap = new ConcurrentHashMap<>();

  protected abstract T createTemplate(
      StringRedisTemplate stringRedisTemplate,
      JacksonRecord jacksonRecord,
      QueueOptions options,
      String name);

  @Override
  public T template(String name) {
    Assert.hasText(name, "name must not be null or empty");

    if (!templateMap.containsKey(name)) {
      initTemplate(name);
    }
    return templateMap.get(name);
  }

  private synchronized void initTemplate(String name) {
    if (templateMap.containsKey(name)) {
      return;
    }

    if (ObjectUtils.anyNull(stringRedisTemplate, jacksonRecord, queueOptionsMap)) {
      throw new IllegalStateException("工厂配置异常, 有属性未注入");
    }

    if (!queueOptionsMap.containsKey(name)) {
      throw MessageErrorCode.MESSAGE_KEY_NOT_CONFIG.throwed().param("name", name);
    }

    QueueOptions options = queueOptionsMap.get(name);
    T handler = createTemplate(stringRedisTemplate, jacksonRecord, options, name);
    templateMap.put(name, handler);
  }
}
