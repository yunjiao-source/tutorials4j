package tutorials4j.framework.message.redis.component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import tutorials4j.framework.message.core.util.MessageUtils;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class DelayMessageHandler {
  private final RedisTemplate<String, String> stringRedisTemplate;

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

  private final AtomicBoolean running = new AtomicBoolean(true);
  private final String sourceQueryName;
  private final String targetQueueName;
  private final Duration sleepWhenException;

  public DelayMessageHandler(
      RedisTemplate<String, String> stringRedisTemplate,
      String sourceQueryName,
      String targetQueueName,
      Duration sleepWhenException) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.targetQueueName = targetQueueName;
    this.sleepWhenException = sleepWhenException;
    this.sourceQueryName = sourceQueryName;

    startTransfer();
  }

  public void addMessage(String message, Duration delayTime) {
    Assert.notNull(message, "message must not be null");
    Assert.notNull(delayTime, "delayTime must not be null");

    long triggerTime = System.currentTimeMillis() + delayTime.toMillis();
    stringRedisTemplate.opsForZSet().add(sourceQueryName, message, triggerTime);
  }

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("消息消费停止，queueName={}", sourceQueryName);
    }

    running.set(false);
  }

  protected void transferExpiredMessages() {
    while (running.get()) {
      try {
        Long count =
            stringRedisTemplate.execute(
                SCRIPT_TRANSFER,
                List.of(sourceQueryName, targetQueueName),
                String.valueOf(System.currentTimeMillis()));
        if (count == null || count == 0) {
          continue;
        }

        if (log.isDebugEnabled()) {
          log.debug(
              "成功转移消息, sourceQueryName={}, targetQueueName={}, count={}",
              sourceQueryName,
              targetQueueName,
              count);
        }
      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error("转移消息异常, sourceQueryName={}, message={}", sourceQueryName, e.getMessage());
        MessageUtils.sleepForWait(sleepWhenException);
      }
    }
  }

  protected void startTransfer() {
    String name = sourceQueryName + "-2-" + targetQueueName;
    Thread thread = new Thread(this::transferExpiredMessages);
    thread.setName(name.replace(":", "_"));
    thread.start();
  }
}
