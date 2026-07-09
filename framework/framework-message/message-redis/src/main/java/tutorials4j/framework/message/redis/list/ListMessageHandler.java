package tutorials4j.framework.message.redis.list;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.core.util.MessageUtils;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class ListMessageHandler {
  private final StringRedisTemplate stringRedisTemplate;
  private final ListMessageConfig config;
  private final JacksonRecord jacksonRecord;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public String send(String data) {
    Assert.notNull(data, "data must not be null");

    BaseRedisMessage message = new BaseRedisMessage();
    message.defaultValue();
    message.setData(data);
    message.setQueueName(config.queueName());
    return send(message);
  }

  public String send(BaseRedisMessage message) {
    Assert.notNull(message, "message must not be null");

    String currentQueueName = config.queueName();
    if (!Objects.equals(message.getQueueName(), currentQueueName)) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("Handler queue name", currentQueueName)
          .param("Message queue name", message.getQueueName());
    }

    stringRedisTemplate.opsForList().leftPush(currentQueueName, jacksonRecord.toJson(message));
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

  public void consumer(ListRedisMessageConsumer consumer) {
    Assert.notNull(consumer, "consumer must not be null or empty");

    while (running.get()) {
      try {
        String message =
            stringRedisTemplate
                .opsForList()
                .rightPop(
                    config.queueName(), config.blockTimeout().toMillis(), TimeUnit.MILLISECONDS);
        if (StringUtils.isBlank(message)) {
          continue;
        }

        BaseRedisMessage baseRedisMessage = jacksonRecord.toObject(message, BaseRedisMessage.class);
        try {
          consumer.handleMessage(baseRedisMessage);
        } catch (Exception e) {
          consumer.handleMessageWhenError(baseRedisMessage, e);
        }
      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error("消息消费异常：queueName = {}, error = {}", config.queueName(), e.getMessage());

        // 休眠，避免错误信息刷屏
        MessageUtils.sleepForWait(config.sleepTimeWhenException());
      }
    }
  }
}
