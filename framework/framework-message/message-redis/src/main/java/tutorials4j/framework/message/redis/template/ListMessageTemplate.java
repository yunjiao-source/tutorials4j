package tutorials4j.framework.message.redis.template;

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
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.core.util.MessageUtils;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.bean.ListMessageConfig;
import tutorials4j.framework.message.redis.component.DelayMessageHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ListMessageTemplate {
  private final RedisTemplate<String, String> stringRedisTemplate;
  private final JacksonRecord jacksonRecord;
  private final ListMessageConfig config;
  private final String mainQueueName;
  private final String processQueueName;
  private final String delayQueueName;
  private final String deadLetterQueueName;
  private final DelayMessageHandler delayMessageHandler;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public ListMessageTemplate(
      RedisTemplate<String, String> stringRedisTemplate,
      JacksonRecord jacksonRecord,
      ListMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jacksonRecord = jacksonRecord;
    this.config = config;
    this.mainQueueName = MessageConsts.getMessageQueueMain(config.queueName());
    this.processQueueName = MessageConsts.getMessageQueueProcess(config.queueName());
    this.delayQueueName = MessageConsts.getMessageQueueDelay(config.queueName());
    this.deadLetterQueueName = MessageConsts.getMessageQueueDeadLetter(config.queueName());

    delayMessageHandler =
        new DelayMessageHandler(
            stringRedisTemplate, delayQueueName, deadLetterQueueName, config.sleepWhenExcption());
  }

  public String send(String data) {
    Assert.notNull(data, "data must not be null");

    BaseRedisMessage message =
        BaseRedisMessage.builder()
            .id(SnowflakeUtils.nextIdStr())
            .timestamp(Instant.now())
            .queueName(config.queueName())
            .retryCount(0)
            .data(data)
            .build();

    return send(message);
  }

  public String send(BaseRedisMessage message) {
    Assert.notNull(message, "message must not be null");

    if (!Objects.equals(message.queueName(), config.queueName())) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("template key", config.queueName())
          .param("message key", message.queueName());
    }

    stringRedisTemplate.opsForList().leftPush(mainQueueName, jacksonRecord.toJson(message));
    return message.id();
  }

  public Map<String, Long> getQueueStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("main", stringRedisTemplate.opsForList().size(mainQueueName));
    stats.put("processing", stringRedisTemplate.opsForList().size(processQueueName));
    stats.put("deadLetter", stringRedisTemplate.opsForList().size(deadLetterQueueName));
    return stats;
  }

  public void consumerDeadLetter(Consumer<BaseRedisMessage> consumer) {
    while (running.get()) {
      try {
        String message =
            stringRedisTemplate
                .opsForList()
                .rightPop(
                    deadLetterQueueName, config.blockTimeout().toMillis(), TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          continue;
        }

        BaseRedisMessage baseRedisMessage = jacksonRecord.toObject(message, BaseRedisMessage.class);
        if (baseRedisMessage.retryCount() >= config.maxRetryCount()) {
          consumer.accept(baseRedisMessage);
        } else {
          send(baseRedisMessage.cloneAndIncreaseRetryCount());
        }

      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error("消费死信消息异常, queueName={}, message={}", config.queueName(), e.getMessage());
        MessageUtils.sleepForWait(config.sleepWhenExcption());
      }
    }
  }

  public void consumerMain(Function<BaseRedisMessage, Boolean> function) {
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
          continue;
        }

        Boolean success = function.apply(jacksonRecord.toObject(message, BaseRedisMessage.class));
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
        MessageUtils.sleepForWait(config.sleepWhenExcption());
      }
    }
  }

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("消息消费停止，queueName={}", config.queueName());
    }

    running.set(false);
    delayMessageHandler.shutdown();
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
    // 发送到延时队列
    delayMessageHandler.addMessage(message, config.delayTimeout());
    if (log.isDebugEnabled()) {
      log.debug("消息进入延时队列, queueName={}", config.queueName());
    }
  }
}
