package tutorials4j.framework.message.redis.template;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
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
import tutorials4j.framework.message.redis.bean.DelayRedisMessage;
import tutorials4j.framework.message.redis.bean.ZSetMessageConfig;
import tutorials4j.framework.message.redis.component.DelayMessageHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ZSetMessageTemplate {
  private final RedisTemplate<String, String> stringRedisTemplate;
  private final JacksonRecord jacksonRecord;
  private final ZSetMessageConfig config;
  private final String processQueueName;
  private final String deadLetterQueueName;
  private final DelayMessageHandler delayMessageHandler2Prcoess;
  private final DelayMessageHandler delayMessageHandler2DeadLetter;
  private final AtomicBoolean running = new AtomicBoolean(true);

  public ZSetMessageTemplate(
      RedisTemplate<String, String> stringRedisTemplate,
      JacksonRecord jacksonRecord,
      ZSetMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jacksonRecord = jacksonRecord;
    this.config = config;
    this.processQueueName = MessageConsts.getMessageQueueProcess(config.queueName());
    this.deadLetterQueueName = MessageConsts.getMessageQueueDeadLetter(config.queueName());

    String mainQueueName = MessageConsts.getMessageQueueMain(config.queueName());
    String delayQueueName = MessageConsts.getMessageQueueDelay(config.queueName());
    delayMessageHandler2Prcoess =
        new DelayMessageHandler(
            stringRedisTemplate, mainQueueName, processQueueName, config.sleepWhenExcption());
    delayMessageHandler2DeadLetter =
        new DelayMessageHandler(
            stringRedisTemplate, delayQueueName, deadLetterQueueName, config.sleepWhenExcption());
  }

  public String addTask(String data, Duration delayTime) {
    Assert.notNull(data, "data must not be null");
    Assert.notNull(delayTime, "delayTime must not be null");

    BaseRedisMessage baseRedisMessage =
        BaseRedisMessage.builder()
            .id(SnowflakeUtils.nextIdStr())
            .timestamp(Instant.now())
            .queueName(config.queueName())
            .retryCount(0)
            .data(data)
            .build();
    DelayRedisMessage message =
        DelayRedisMessage.builder().baseMessage(baseRedisMessage).delayTime(delayTime).build();
    return addTask(message);
  }

  public String addTask(DelayRedisMessage message) {
    Assert.notNull(message, "message must not be null");

    if (!Objects.equals(message.baseMessage().queueName(), config.queueName())) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("template key", config.queueName())
          .param("message key", message.baseMessage().queueName());
    }

    delayMessageHandler2Prcoess.addMessage(jacksonRecord.toJson(message), message.delayTime());
    return message.baseMessage().id();
  }

  public void consumerProcess(Consumer<DelayRedisMessage> consumer) {
    while (running.get()) {
      String message = null;
      try {
        message =
            stringRedisTemplate
                .opsForList()
                .rightPop(
                    processQueueName, config.blockTimeout().toMillis(), TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          continue;
        }

        consumer.accept(jacksonRecord.toObject(message, DelayRedisMessage.class));
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

  public void consumerDeadLetter(Consumer<DelayRedisMessage> consumer) {
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

        DelayRedisMessage delayRedisMessage =
            jacksonRecord.toObject(message, DelayRedisMessage.class);
        if (delayRedisMessage.baseMessage().retryCount() >= config.maxRetryCount()) {
          consumer.accept(delayRedisMessage);
        } else {
          addTask(delayRedisMessage.cloneAndIncreaseRetryCount());
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

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("消息消费停止，queueName={}", config.queueName());
    }

    running.set(false);
    delayMessageHandler2Prcoess.shutdown();
    delayMessageHandler2DeadLetter.shutdown();
  }

  protected void fail(String message) {
    if (StringUtils.isBlank(message)) {
      return;
    }
    // 发送到延时队列
    delayMessageHandler2DeadLetter.addMessage(message, config.delayTimeout());
    if (log.isDebugEnabled()) {
      log.debug("消息进入延时队列, queueName={}", config.queueName());
    }
  }
}
