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
public class ListMessageHandlerFactory {
  public static final ListMessageHandlerFactory instance = new ListMessageHandlerFactory();

  @Setter private StringRedisTemplate stringRedisTemplate;
  @Setter private JacksonRecord jacksonRecord;
  @Setter private Map<String, QueueOptions> queueOptionsMap;
  private final Map<String, ListMessageHandler> handlereMap = new ConcurrentHashMap<>();

  public ListMessageHandler handler(String key) {
    Assert.hasText(key, "key must not be null or empty");

    if (!handlereMap.containsKey(key)) {
      initHandler(key);
    }
    return handlereMap.get(key);
  }

  private synchronized void initHandler(String key) {
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
            .queueName(key)
            .queueName(MessageConsts.getMessageQueueMain(key))
            .blockTimeout(options.getBlockTimeout())
            .sleepTimeWhenException(options.getSleepTimeWhenException())
            .build();
    config.validate();

    ListMessageHandler handler = new ListMessageHandler(stringRedisTemplate, config, jacksonRecord);
    handlereMap.put(key, handler);
  }
}
