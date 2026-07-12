package tutorials4j.framework.message.redis.list;

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
public class ListMessageTemplate {
  private final StringRedisTemplate stringRedisTemplate;
  private final JacksonRecord jacksonRecord;
  private final TemplateConfig config;
  private final QueueOptions options;
  private DelayMessageProcessor processor;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public String send(String data) {
    Assert.hasText(data, "data must not be null or empty");
    RedisMessage message = RedisMessage.defaultValue().setName(config.name()).setData(data);
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

    stringRedisTemplate.opsForList().leftPush(config.queueName(), jacksonRecord.toJson(message));
    return message.getId();
  }

  public Long getQueueSize() {
    return stringRedisTemplate.opsForList().size(config.queueName());
  }

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("消息消费停止，queueName={}", config.queueName());
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
                    config.queueName(),
                    options.getBlockTimeout().toMillis(),
                    TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          if (options.getRetry().isEnabled()) {
            getProcessor().transferExpiredMessages();
          }
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
        // 休眠，避免错误信息刷屏
        MessageUtils.sleepForWait(options.getSleepTimeWhenException());
      }
    }
  }

  private DelayMessageProcessor getProcessor() {
    if (processor == null) {
      synchronized (this) {
        if (processor != null) {
          return processor;
        }

        String delayQueueName = config.queueName() + MessageConsts.MESSAGE_QUEUE_SUFFIX_DELAY;
        processor =
            new DelayMessageProcessor(
                stringRedisTemplate, jacksonRecord, delayQueueName, config.queueName());
      }
    }

    return processor;
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

    // 重试
    message.increaseRetryCount();
    if (message.getDelayTime() == null) {
      message.setDelayTime(options.getRetry().getDelayTime());
    }
    getProcessor().send(message);
    return true;
  }
}
