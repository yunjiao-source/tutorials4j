package tutorials4j.framework.message.redis.template;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.bean.DelayRedisMessage;
import tutorials4j.framework.message.redis.bean.ZSetMessageConfig;

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
  private final String mainQueueName;
  private final String processQueueName;
  private final String deadLetterQueueName;

  private final AtomicBoolean running = new AtomicBoolean(true);

  /** 转移到期任务 */
  private static final RedisScript<Long> SCRIPT_TRANSFER =
      new DefaultRedisScript<>(
          """
          local messages = redis.call('ZRANGEBYSCORE', KEYS[1], 0, ARGV[1])
            if #messages > 0 then
                redis.call('ZREM', KEYS[1], unpack(messages))
                redis.call('RPUSH', KEYS[2], unpack(messages))
            end
            return #messages
      """,
          Long.class);

  public ZSetMessageTemplate(
      RedisTemplate<String, String> stringRedisTemplate,
      JacksonRecord jacksonRecord,
      ZSetMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jacksonRecord = jacksonRecord;
    this.config = config;
    this.mainQueueName = config.getMainQueueName();
    this.processQueueName = config.getProcessQueueName();
    this.deadLetterQueueName = config.getDeadLetterQueueName();

    startTransfer();
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

    long triggerTime = System.currentTimeMillis() + message.delayTime().toMillis();
    stringRedisTemplate.opsForZSet().add(mainQueueName, jacksonRecord.toJson(message), triggerTime);
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
        sleepForWait(config.sleepWhenExcption());
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

        consumer.accept(jacksonRecord.toObject(message, DelayRedisMessage.class));
      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error("消费死信消息异常, queueName={}, message={}", config.queueName(), e.getMessage());
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

  protected void transferExpiredMessages() {
    while (running.get()) {
      try {
        Long count =
            stringRedisTemplate.execute(
                SCRIPT_TRANSFER,
                List.of(mainQueueName, processQueueName),
                String.valueOf(System.currentTimeMillis()));
        if (count == null || count == 0) {
          continue;
        }

        if (log.isDebugEnabled()) {
          log.debug("成功转移消息, queueName={}, count={}", config.queueName(), count);
        }
      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error("转移消息异常, queueName={}, message={}", config.queueName(), e.getMessage());
        sleepForWait(config.sleepWhenExcption());
      }
    }
  }

  protected void fail(String message) {
    if (StringUtils.isBlank(message)) {
      return;
    }
    stringRedisTemplate.opsForList().leftPush(deadLetterQueueName, message);
    if (log.isDebugEnabled()) {
      log.debug("消息进入死信, queueName={}", config.queueName());
    }
  }

  private void startTransfer() {
    Thread thread = new Thread(this::transferExpiredMessages);
    thread.setName("zset-message-transfer");
    thread.start();
  }

  private void sleepForWait(Duration waitTime) {
    // 避免连接异常时快速空转，短暂等待后重试
    try {
      TimeUnit.MILLISECONDS.sleep(waitTime.toMillis());
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt(); // 保留中断状态
    }
  }
}
