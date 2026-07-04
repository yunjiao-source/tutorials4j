package tutorials4j.framework.message.redis.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.common.core.exception.WrapCheckException;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.bean.ListMessageConfig;
import tutorials4j.framework.message.redis.bean.RedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ListMessageTemplate {
  private final RedisTemplate<String, String> stringRedisTemplate;
  private final ObjectMapper objectMapper;
  private final ListMessageConfig config;
  private final String mainQueueName;
  private final String processQueueName;
  private final String deadLetterQueueName;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public ListMessageTemplate(
      RedisTemplate<String, String> stringRedisTemplate,
      ObjectMapper objectMapper,
      ListMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.objectMapper = objectMapper;
    this.config = config;
    this.mainQueueName = config.getMainQueueName();
    this.processQueueName = config.getProcessQueueName();
    this.deadLetterQueueName = config.getDeadLetterQueueName();
  }

  public String send(Map<String, String> data) {
    Assert.notNull(data, "data must not be null");

    RedisMessage message =
        RedisMessage.builder()
            .id(SnowflakeUtils.nextIdStr())
            .timestamp(Instant.now())
            .queueName(config.queueName())
            .retryCount(0)
            .data(data)
            .build();

    return send(message);
  }

  public String send(RedisMessage message) {
    Assert.notNull(message, "message must not be null");

    if (!Objects.equals(message.queueName(), config.queueName())) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("template key", config.queueName())
          .param("message key", message.queueName());
    }

    stringRedisTemplate.opsForList().leftPush(mainQueueName, serialize(message));
    return message.id();
  }

  public Map<String, Long> getQueueStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("main", stringRedisTemplate.opsForList().size(mainQueueName));
    stats.put("processing", stringRedisTemplate.opsForList().size(processQueueName));
    stats.put("deadLetter", stringRedisTemplate.opsForList().size(deadLetterQueueName));
    return stats;
  }

  public void consumerDeadLetter(Consumer<RedisMessage> consumer) {
    while (running.get()) {
      try {
        String message =
            stringRedisTemplate
                .opsForList()
                .rightPop(
                    deadLetterQueueName, config.blockTimeout().toMillis(), TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          sleepForWait(config.sleepWhenNoData());
          continue;
        }

        consumer.accept(deserialize(message));
      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error("消费死信消息异常, queueName={}, message={}", config.queueName(), e.getMessage());
        sleepForWait(config.sleepWhenExcption());
      }
    }
  }

  public void consumerMain(Function<RedisMessage, Boolean> function) {
    while (running.get()) {
      String message = null;
      try {
        message =
            stringRedisTemplate
                .opsForList()
                .rightPopAndLeftPush(
                    mainQueueName,
                    processQueueName,
                    config.blockTimeout().toMillis(),
                    TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          sleepForWait(config.sleepWhenNoData());
          continue;
        }

        Boolean success = function.apply(deserialize(message));
        if (success != null && success) {
          ack(message);
        } else {
          fail(message);
        }

      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        fail(message);
        log.error("消费消息异常, queueName={}, message={}", config.queueName(), e.getMessage());
        sleepForWait(config.sleepWhenExcption());
      }
    }
  }

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("消息消费停止，queueName={}", config.queueName());
    }

    running.set(false);
  }

  protected void ack(String message) {
    try {
      Long count = stringRedisTemplate.opsForList().remove(processQueueName, 1, message);
      if (count <= 0) {
        log.error("消息确认失败，queueName={}, message={}", processQueueName, message);
      }
    } catch (Exception e) {
      log.error("确认消息异常, queueName={}, messge={}", config.queueName(), e.getMessage());
    }
  }

  protected void fail(String message) {
    if (StringUtils.isBlank(message)) {
      return;
    }
    ack(message);
    stringRedisTemplate.opsForList().leftPush(deadLetterQueueName, message);
    if (log.isDebugEnabled()) {
      log.debug("消息进入死信, queueName={}", config.queueName());
    }
  }

  private void sleepForWait(Duration waitTime) {
    // 避免连接异常时快速空转，短暂等待后重试
    try {
      TimeUnit.MILLISECONDS.sleep(waitTime.toMillis());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt(); // 保留中断状态
    }
  }

  private String serialize(RedisMessage message) {
    try {
      return objectMapper.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("对象转换字符串异常", e);
    }
  }

  private RedisMessage deserialize(String message) {
    try {
      return objectMapper.readValue(message, RedisMessage.class);
    } catch (JsonProcessingException e) {
      throw new WrapCheckException("字符串转换对象异常", e);
    }
  }
}
