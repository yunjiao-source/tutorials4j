package tutorials4j.framework.message.redis.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.bean.ListMessageConfig;
import tutorials4j.framework.message.redis.properties.RedisMessageProperties.ListQueueOptions;
import tutorials4j.framework.message.redis.template.ListMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ListMessageFactory {
  public static final ListMessageFactory instance = new ListMessageFactory();

  @Setter private RedisTemplate<String, String> stringRedisTemplate;
  @Setter private ObjectMapper objectMapper;
  @Setter private Map<String, ListQueueOptions> listQueueOptionsMap;
  private final Map<String, ListMessageTemplate> templateMap = new ConcurrentHashMap<>();

  public ListMessageTemplate template(String queueName) {
    Assert.hasText(queueName, "queueName must not be null or empty");

    if (!templateMap.containsKey(queueName)) {
      initTemplate(queueName);
    }
    return templateMap.get(queueName);
  }

  @PreDestroy
  public void destroy() {
    if (log.isDebugEnabled()) {
      log.debug("关闭消息工厂，数量是 {}", templateMap.size());
    }
    templateMap.forEach((k, v) -> v.shutdown());
  }

  private synchronized void initTemplate(String queueName) {
    if (templateMap.containsKey(queueName)) {
      return;
    }

    if (ObjectUtils.anyNull(stringRedisTemplate, objectMapper, listQueueOptionsMap)) {
      throw new IllegalStateException("工厂配置异常, 有属性未注入");
    }

    if (!listQueueOptionsMap.containsKey(queueName)) {
      throw MessageErrorCode.MESSAGE_KEY_NOT_CONFIG.throwed().param("queueName", queueName);
    }

    ListQueueOptions options = listQueueOptionsMap.get(queueName);
    ListMessageConfig config =
        ListMessageConfig.builder()
            .queueName(queueName)
            .sleepWhenExcption(options.getSleepWhenException())
            .sleepWhenNoData(options.getSleepWhenNoData())
            .blockTimeout(options.getBlockTimeout())
            .build();

    ListMessageTemplate template =
        new ListMessageTemplate(stringRedisTemplate, objectMapper, config);
    templateMap.put(queueName, template);
  }
}
