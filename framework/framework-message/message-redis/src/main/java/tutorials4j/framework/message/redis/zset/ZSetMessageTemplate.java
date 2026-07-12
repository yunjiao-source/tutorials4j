package tutorials4j.framework.message.redis.zset;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.core.util.MessageUtils;
import tutorials4j.framework.message.redis.bean.RedisMessage;
import tutorials4j.framework.message.redis.properties.QueueOptions;
import tutorials4j.framework.message.redis.properties.QueueOptions.RetryOptions;
import tutorials4j.framework.message.redis.template.DelayMessageProcessor;
import tutorials4j.framework.message.redis.template.RedisMessageConsumer;
import tutorials4j.framework.message.redis.template.TemplateConfig;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ZSetMessageTemplate {
  private final StringRedisTemplate stringRedisTemplate;
  private final JacksonRecord jacksonRecord;
  private final TemplateConfig config;
  private final QueueOptions options;
  private DelayMessageProcessor processor;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public String send(String data, Duration delayTime) {
    Assert.hasText(data, "data must not be null or empty");
    Assert.notNull(data, "delayTime must not be null");
    RedisMessage message =
        RedisMessage.defaultValue().setDelayTime(delayTime).setName(config.name()).setData(data);
    return send(message);
  }

  public String send(RedisMessage message) {
    Assert.notNull(message, "message must not be null");

    if (!Objects.equals(message.getName(), config.name())) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("config", config.name())
          .param("message", message.getName());
    }

    return getProcessor().send(message);
  }

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("延时消息消费停止，queryName={}", config.queueName());
    }

    running.set(false);
  }

  public void consumer(RedisMessageConsumer consumer) {
    Assert.notNull(consumer, "consumer must not be null or empty");

    while (running.get()) {
      String message = null;
      try {
        message =
            stringRedisTemplate
                .opsForList()
                .rightPop(
                    getProcessQueueName(),
                    options.getBlockTimeout().toMillis(),
                    TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          getProcessor().transferExpiredMessages();
          continue;
        }

        RedisMessage redisMessage = jacksonRecord.toObject(message, RedisMessage.class);
        try {
          consumer.handleMessage(redisMessage);
        } catch (Exception e) {
          boolean canRetry = doRetry(redisMessage, options.getRetry(), e);
          if (!canRetry) {
            // 不重试
            consumer.handleMessageWhenError(redisMessage, e);
          }
        }

      } catch (Exception e) {
        log.error("消息消费异常：queryName = {}, message={}", config.queueName(), message, e);
        if (Thread.currentThread().isInterrupted()) {
          break;
        }
        MessageUtils.sleepForWait(options.getSleepTimeWhenException());
      }
    }
  }

  protected boolean doRetry(RedisMessage message, RetryOptions retry, Throwable throwable) {
    message.addReason(throwable.getMessage());

    if (!retry.isEnabled()) {
      // 不执行重试策略
      return false;
    }
    if (message.getRetryCount() >= retry.getMaxCount()) {
      message.addReason("重试超过最大次数, maxCount=" + retry.getMaxCount());
      return false;
    }

    Duration elapsed = Duration.between(message.getTimestamp(), Instant.now());
    if (elapsed.compareTo(retry.getExpireTime()) > 0) {
      message.addReason(
          "重试数据已过期, timestamp=" + message.getTimestamp() + ", expireTime=" + retry.getExpireTime());
      return false;
    }

    message.increaseRetryCount();
    // 重试
    send(message);
    return true;
  }

  private DelayMessageProcessor getProcessor() {
    if (processor == null) {
      synchronized (this) {
        if (processor != null) {
          return processor;
        }

        processor =
            new DelayMessageProcessor(
                stringRedisTemplate, jacksonRecord, config.queueName(), getProcessQueueName());
      }
    }

    return processor;
  }

  private String getProcessQueueName() {
    return config.queueName() + MessageConsts.MESSAGE_QUEUE_SUFFIX_PROCESS;
  }
}
