package tutorials4j.framework.message.redis.zset;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.exception.MessageErrorCode;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ZSetMessageHandler {
  private final StringRedisTemplate stringRedisTemplate;
  private final ZSetMessageConfig config;
  private final JacksonRecord jacksonRecord;

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

  public String addTask(String data, Duration delayTime) {
    Assert.notNull(data, "data must not be null");
    Assert.notNull(delayTime, "delayTime must not be null");

    DelayRedisMessage message = new DelayRedisMessage();
    message.defaultValue();
    message.setData(data);
    message.setQueueName(config.delayQueueName());
    message.setDelayTime(delayTime);
    return addTask(message);
  }

  public String addTask(DelayRedisMessage message) {
    Assert.notNull(message, "message must not be null");

    String currentQueueName = config.delayQueueName();
    if (!Objects.equals(message.getQueueName(), config.delayQueueName())) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("Handler queue name", currentQueueName)
          .param("Message queue name", message.getQueueName());
    }

    long triggerTime = System.currentTimeMillis() + message.getDelayTime().toMillis();
    stringRedisTemplate
        .opsForZSet()
        .add(currentQueueName, jacksonRecord.toJson(message), triggerTime);
    return message.getData();
  }

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("延时消息消费停止，delayQueryName={}", config.delayQueueName());
    }

    running.set(false);
  }

  public void consumer(DelayMessageConsumer consumer) {
    Assert.notNull(consumer, "consumer must not be null or empty");
    while (running.get()) {
      try {
        String message =
            stringRedisTemplate
                .opsForList()
                .rightPop(
                    config.processQueueName(),
                    config.blockTimeout().toMillis(),
                    TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          transferExpiredMessages();
          continue;
        }

        DelayRedisMessage baseRedisMessage =
            jacksonRecord.toObject(message, DelayRedisMessage.class);
        try {
          consumer.handleMessage(baseRedisMessage);
        } catch (Exception e) {
          consumer.handleMessageWhenError(baseRedisMessage, e);
        }

      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error(
            "延时消息消费异常：delayQueryName = {}, error = {}", config.delayQueueName(), e.getMessage());

        // 休眠，避免错误信息刷屏
        try {
          TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  protected Long transferExpiredMessages() {
    Long count =
        stringRedisTemplate.execute(
            SCRIPT_TRANSFER,
            List.of(config.delayQueueName(), config.processQueueName()),
            String.valueOf(System.currentTimeMillis()));
    if (log.isDebugEnabled() && count > 0) {
      log.debug(
          "成功转移消息, delayQueryName={}, processQueueName={}, count={}",
          config.delayQueueName(),
          config.processQueueName(),
          count);
    }
    return count;
  }
}
