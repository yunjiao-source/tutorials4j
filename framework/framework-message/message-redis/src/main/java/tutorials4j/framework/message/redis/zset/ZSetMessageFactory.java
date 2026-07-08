package tutorials4j.framework.message.redis.zset;

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
public class ZSetMessageFactory {
  public static final ZSetMessageFactory instance = new ZSetMessageFactory();

  @Setter private StringRedisTemplate stringRedisTemplate;
  @Setter private JacksonRecord jacksonRecord;
  @Setter private Map<String, QueueOptions> queueOptionsMap;
  private final Map<String, ZSetMessageHandler> handlereMap = new ConcurrentHashMap<>();

  public ZSetMessageHandler template(String key) {
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
    ZSetMessageConfig config =
        ZSetMessageConfig.builder()
            .delayQueueName(MessageConsts.getMessageQueueMain(key))
            .processQueueName(MessageConsts.getMessageQueueProcess(key))
            .blockTimeout(options.getBlockTimeout())
            .build();

    ZSetMessageHandler handler = new ZSetMessageHandler(stringRedisTemplate, config, jacksonRecord);
    handlereMap.put(key, handler);
  }
}
