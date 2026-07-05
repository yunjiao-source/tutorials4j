package tutorials4j.framework.message.redis.template;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.util.SnowflakeUtils;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.bean.StreamMessageConfig;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class StreamMessageTemplate {
  private static final String PAYLOAD = "payload";
  private final RedisTemplate<String, String> stringRedisTemplate;
  private final JacksonRecord jacksonRecord;
  private final StreamMessageConfig config;
  private final String mainQueueName;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public StreamMessageTemplate(
      RedisTemplate<String, String> stringRedisTemplate,
      JacksonRecord jacksonRecord,
      StreamMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jacksonRecord = jacksonRecord;
    this.config = config;
    this.mainQueueName = config.getMainQueueName();
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

    RecordId recordId =
        stringRedisTemplate
            .opsForStream()
            .add(StreamRecords.objectBacked(message).withStreamKey(mainQueueName));
    return recordId != null ? recordId.getValue() : null;
  }

  public void consumerMain(
      String consumerGroup, String consumerName, Consumer<BaseRedisMessage> consumer) {
    Assert.hasText(consumerGroup, "consumerGroup must not be null or empty");
    Assert.hasText(consumerName, "consumerName must not be null or empty");

    createConsumerGroup(consumerGroup);
    while (running.get()) {
      try {
        org.springframework.data.redis.connection.stream.Consumer streamConsumer =
            org.springframework.data.redis.connection.stream.Consumer.from(
                consumerGroup, consumerName);
        ReadOffset readOffset = ReadOffset.lastConsumed();

        List<ObjectRecord<String, BaseRedisMessage>> messages =
            stringRedisTemplate
                .opsForStream()
                .read(
                    BaseRedisMessage.class,
                    streamConsumer,
                    StreamReadOptions.empty()
                        .count(config.countPreRead())
                        .block(Duration.ofSeconds(config.blockTimeout().toSeconds())),
                    StreamOffset.create(mainQueueName, readOffset));

        if (CollectionUtils.isEmpty(messages)) {
          continue;
        }

        for (ObjectRecord<String, BaseRedisMessage> message : messages) {
          consumer.accept(message.getValue());
          stringRedisTemplate
              .opsForStream()
              .acknowledge(mainQueueName, consumerGroup, message.getId().getValue());
        }
      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

        log.error("消费消息异常, queueName={}, message={}", config.queueName(), e.getMessage());
        sleepForWait(config.sleepWhenExcption());
      }
    }
  }

  public void consumerPending(
      String consumerGroup, String consumerName, Consumer<BaseRedisMessage> consumer) {
    Assert.hasText(consumerGroup, "consumerGroup must not be null or empty");

    createConsumerGroup(consumerGroup);
    while (running.get()) {
      try {
        org.springframework.data.redis.connection.stream.Consumer streamConsumer =
            org.springframework.data.redis.connection.stream.Consumer.from(
                consumerGroup, consumerName);
        ReadOffset readOffset = ReadOffset.lastConsumed();

        List<ObjectRecord<String, BaseRedisMessage>> messages =
            stringRedisTemplate
                .opsForStream()
                .read(
                    BaseRedisMessage.class,
                    streamConsumer,
                    StreamReadOptions.empty()
                        .count(config.countPreRead())
                        .block(Duration.ofSeconds(config.blockTimeout().toSeconds())),
                    StreamOffset.create(mainQueueName, readOffset));

        if (CollectionUtils.isEmpty(messages)) {
          continue;
        }

        for (ObjectRecord<String, BaseRedisMessage> message : messages) {
          consumer.accept(message.getValue());
          stringRedisTemplate
              .opsForStream()
              .acknowledge(mainQueueName, consumerGroup, message.getId().getValue());
        }
      } catch (Exception e) {
        if (Thread.currentThread().isInterrupted()) {
          break;
        }

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

  private void createConsumerGroup(String groupName) {
    try {
      stringRedisTemplate
          .opsForStream()
          .createGroup(mainQueueName, ReadOffset.from("0"), groupName);
    } catch (Exception e) {
      // 消费者组已存在，忽略异常
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
}
