package tutorials4j.framework.message.redis.stream;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.core.util.MessageUtils;
import tutorials4j.framework.message.redis.bean.RedisMessage;
import tutorials4j.framework.message.redis.bean.RedisMessageType;
import tutorials4j.framework.message.redis.template.RedisMessageConsumer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class StreamMessageTemplate {
  private final StringRedisTemplate stringRedisTemplate;
  private final StreamMessageConfig config;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public StreamMessageTemplate(
      StringRedisTemplate stringRedisTemplate, StreamMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.config = config;

    init();
  }

  public String send(RedisMessage message) {
    Assert.notNull(message, "message must not be null");

    if (StringUtils.isBlank(message.getName())) {
      // 新消息没有值，重复时有值
      message.setName(config.name());
    }
    if (!Objects.equals(message.getName(), config.name())) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("config", config.name())
          .param("message", message.getName());
    }

    RecordId recordId =
        stringRedisTemplate
            .opsForStream()
            .add(StreamRecords.objectBacked(message).withStreamKey(config.queueName()));
    return recordId != null ? recordId.getValue() : null;
  }

  public void consumer(String consumerGroup, String consumerName, RedisMessageConsumer consumer) {
    Assert.hasText(consumerGroup, "consumerGroup must not be null or empty");
    Assert.notNull(consumerName, "consumerName must not be null or empty");
    Assert.notNull(consumer, "consumer must not be null");

    String streamKey = config.queueName();

    while (running.get()) {
      try {
        // 构建读取选项：阻塞等待、批量拉取
        StreamReadOptions readOptions =
            StreamReadOptions.empty().block(config.blockTimeout()).count(config.countPreRead());

        // 从消费者组读取消息（从上次消费位置继续）
        List<ObjectRecord<String, RedisMessage>> messages =
            stringRedisTemplate
                .opsForStream()
                .read(
                    RedisMessage.class,
                    Consumer.from(consumerGroup, consumerName),
                    readOptions,
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()));

        if (messages == null || messages.isEmpty()) {
          continue;
        }

        for (ObjectRecord<String, RedisMessage> record : messages) {
          RedisMessage body = record.getValue();
          try {
            consumer.handleMessage(body);
            ack(record);
          } catch (Exception e) {
            ack(record);
            consumer.handleMessageWhenError(body, e);
          }
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

  public long trimByMinId() {
    long threshold = System.currentTimeMillis() - config.retentionTime().toMillis();
    String minId = threshold + "-0";

    RedisCallback<Long> callback =
        (connection) -> {
          return (Long)
              connection.execute(
                  "XTRIM",
                  config.queueName().getBytes(),
                  "MINID".getBytes(),
                  "~".getBytes(),
                  minId.getBytes());
        };
    return stringRedisTemplate.execute(callback);
  }

  public void shutdown() {
    if (log.isDebugEnabled()) {
      log.debug("消息消费停止，queueName={}", config.queueName());
    }
    running.set(false);
  }

  public RedisMessageType getMessageType() {
    return RedisMessageType.stream;
  }

  private void init() {
    try {
      stringRedisTemplate.opsForStream().createGroup(config.queueName(), config.consumerGroup());
    } catch (Exception ignored) {
    }
  }

  private void ack(ObjectRecord<String, RedisMessage> record) {
    stringRedisTemplate.opsForStream().acknowledge(config.consumerGroup(), record);
  }
}
