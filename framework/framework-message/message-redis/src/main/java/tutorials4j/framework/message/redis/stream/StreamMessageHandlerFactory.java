package tutorials4j.framework.message.redis.stream;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.properties.QueueOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class StreamMessageHandlerFactory {
  public static final StreamMessageHandlerFactory instance = new StreamMessageHandlerFactory();

  @Setter private StringRedisTemplate stringRedisTemplate;
  @Setter private Map<String, QueueOptions> queueOptionsMap;
  private final Map<String, StreamMessageHandler> handlereMap = new ConcurrentHashMap<>();

  public StreamMessageHandler handler(String key) {
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

    if (ObjectUtils.anyNull(stringRedisTemplate, queueOptionsMap)) {
      throw new IllegalStateException("工厂配置异常, 有属性未注入");
    }

    if (!queueOptionsMap.containsKey(key)) {
      throw MessageErrorCode.MESSAGE_KEY_NOT_CONFIG.throwed().param("key", key);
    }

    QueueOptions options = queueOptionsMap.get(key);
    StreamMessageConfig config =
        StreamMessageConfig.builder()
            .name(key)
            .queueName(MessageConsts.getMessageQueueMain(key))
            .consumerGroup(options.getConsumerGroup())
            .blockTimeout(options.getBlockTimeout())
            .countPreRead(options.getCountPreRead())
            .sleepTimeWhenException(options.getSleepTimeWhenException())
            .retentionTime(options.getRetentionTime())
            .build();
    config.validate();

    StreamMessageHandler handler = new StreamMessageHandler(stringRedisTemplate, config);
    handlereMap.put(key, handler);
  }
}
