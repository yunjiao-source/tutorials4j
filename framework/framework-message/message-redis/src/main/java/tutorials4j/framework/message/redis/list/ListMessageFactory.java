package tutorials4j.framework.message.redis.list;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.properties.QueueOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ListMessageFactory {
  public static final ListMessageFactory instance = new ListMessageFactory();

  @Setter private StringRedisTemplate stringRedisTemplate;
  @Setter private JacksonRecord jacksonRecord;
  @Setter private Map<String, QueueOptions> queueOptionsMap;
  private final Map<String, ListMessageHandler> handlereMap = new ConcurrentHashMap<>();

  public ListMessageHandler template(String key) {
    Assert.hasText(key, "key must not be null or empty");

    if (!handlereMap.containsKey(key)) {
      initTemplate(key);
    }
    return handlereMap.get(key);
  }

  private synchronized void initTemplate(String key) {
    if (handlereMap.containsKey(key)) {
      return;
    }

    if (ObjectUtils.anyNull(stringRedisTemplate, jacksonRecord, queueOptionsMap)) {
      throw new IllegalStateException("工厂配置异常, 有属性未注入");
    }

    if (!queueOptionsMap.containsKey(key)) {
      throw MessageErrorCode.MESSAGE_KEY_NOT_CONFIG.throwed().param("key", key);
    }

    QueueOptions options = queueOptionsMap.get(key);
    ListMessageConfig config =
        ListMessageConfig.builder()
            .queueName(MessageConsts.getMessageQueueMain(key))
            .blockTimeout(options.getBlockTimeout())
            .build();

    ListMessageHandler handler = new ListMessageHandler(stringRedisTemplate, config, jacksonRecord);
    handlereMap.put(key, handler);
  }
}
