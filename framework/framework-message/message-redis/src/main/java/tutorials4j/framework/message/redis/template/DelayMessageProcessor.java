package tutorials4j.framework.message.redis.template;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.redis.bean.RedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class DelayMessageProcessor {
  private final StringRedisTemplate stringRedisTemplate;
  private final JacksonRecord jacksonRecord;
  private final String delayQueueName;
  private final String processQueueName;

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

  public String send(RedisMessage message) {
    Assert.notNull(message, "message must not be null");
    Assert.notNull(message.getDelayTime(), "message delayTime must not be null");

    long triggerTime = System.currentTimeMillis() + message.getDelayTime().toMillis();
    stringRedisTemplate
        .opsForZSet()
        .add(delayQueueName, jacksonRecord.toJson(message), triggerTime);
    return message.getId();
  }

  public Long transferExpiredMessages() {
    long count =
        stringRedisTemplate.execute(
            SCRIPT_TRANSFER,
            List.of(delayQueueName, processQueueName),
            String.valueOf(System.currentTimeMillis()));
    if (log.isDebugEnabled() && count > 0) {
      log.debug(
          "成功转移消息, delayQueueName={}, processQueueName={}, count={}",
          delayQueueName,
          processQueueName,
          count);
    }
    return count;
  }
}
