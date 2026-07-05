package tutorials4j.framework.message.redis.factory;

import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.bean.ZSetMessageConfig;
import tutorials4j.framework.message.redis.properties.QueueOptions;
import tutorials4j.framework.message.redis.template.ZSetMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ZSetMessageFactory {
  public static final String MESSAGE_TYPE = "zset";
  public static final ZSetMessageFactory instance = new ZSetMessageFactory();

  @Setter private RedisTemplate<String, String> stringRedisTemplate;
  @Setter private JacksonRecord jacksonRecord;
  @Setter private Map<String, QueueOptions> queueOptionsMap;
  private final Map<String, ZSetMessageTemplate> templateMap = new ConcurrentHashMap<>();

  public ZSetMessageTemplate template(String queueName) {
    Assert.hasText(queueName, "queueName must not be null or empty");

    if (!templateMap.containsKey(queueName)) {
      initTemplate(queueName);
    }
    return templateMap.get(queueName);
  }

  @PreDestroy
  public void destroy() {
    if (log.isDebugEnabled() && !templateMap.isEmpty()) {
      log.debug("关闭消息工厂，数量是 {}", templateMap.size());
    }
    templateMap.forEach((k, v) -> v.shutdown());
  }

  private synchronized void initTemplate(String queueName) {
    if (templateMap.containsKey(queueName)) {
      return;
    }

    if (ObjectUtils.anyNull(stringRedisTemplate, jacksonRecord, queueOptionsMap)) {
      throw new IllegalStateException("工厂配置异常, 有属性未注入");
    }

    if (!queueOptionsMap.containsKey(queueName)) {
      throw MessageErrorCode.MESSAGE_KEY_NOT_CONFIG.throwed().param("queueName", queueName);
    }

    QueueOptions options = queueOptionsMap.get(queueName);
    ZSetMessageConfig config =
        ZSetMessageConfig.builder()
            .queueName(MESSAGE_TYPE + ":" + queueName)
            .sleepWhenExcption(options.getSleepWhenException())
            .blockTimeout(options.getBlockTimeout())
            .delayTimeout(options.getDelayTimeout())
            .maxRetryCount(options.getMaxRetryCount())
            .build();

    ZSetMessageTemplate template =
        new ZSetMessageTemplate(stringRedisTemplate, jacksonRecord, config);
    templateMap.put(queueName, template);
  }
}
