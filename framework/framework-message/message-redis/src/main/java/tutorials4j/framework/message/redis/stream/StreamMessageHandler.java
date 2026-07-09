package tutorials4j.framework.message.redis.stream;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
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
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class StreamMessageHandler {
  private final StringRedisTemplate stringRedisTemplate;
  private final StreamMessageConfig config;

  private final AtomicBoolean running = new AtomicBoolean(true);

  public StreamMessageHandler(StringRedisTemplate stringRedisTemplate, StreamMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.config = config;

    init();
  }

  public String send(String data) {
    Assert.notNull(data, "data must not be null");

    BaseRedisMessage message = new BaseRedisMessage();
    message.defaultValue();
    message.setQueueName(config.queueName());
    message.setData(data);

    return send(message);
  }

  public String send(BaseRedisMessage message) {
    Assert.notNull(message, "message must not be null");

    if (!Objects.equals(message.getQueueName(), config.queueName())) {
      throw MessageErrorCode.MESSAGE_KEY_MISMATCH
          .throwed()
          .param("template key", config.queueName())
          .param("message key", message.getQueueName());
    }

    RecordId recordId =
        stringRedisTemplate
            .opsForStream()
            .add(StreamRecords.objectBacked(message).withStreamKey(config.queueName()));
    return recordId != null ? recordId.getValue() : null;
  }

  public void consumer(String consumerName, StreamMessageConsumer consumer) {
    Assert.notNull(consumerName, "consumerName must not be null or empty");
    Assert.notNull(consumer, "consumer must not be null");

    String streamKey = config.queueName();
    String consumerGroup = config.consumerGroup();

    while (running.get()) {
      try {
        // 构建读取选项：阻塞等待、批量拉取
        StreamReadOptions readOptions =
            StreamReadOptions.empty().block(config.blockTimeout()).count(config.countPreRead());

        // 从消费者组读取消息（从上次消费位置继续）
        List<ObjectRecord<String, BaseRedisMessage>> messages =
            stringRedisTemplate
                .opsForStream()
                .read(
                    BaseRedisMessage.class,
                    Consumer.from(consumerGroup, consumerName),
                    readOptions,
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()));

        if (messages == null || messages.isEmpty()) {
          continue;
        }

        for (ObjectRecord<String, BaseRedisMessage> record : messages) {
          BaseRedisMessage body = record.getValue();
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

  private void init() {
    try {
      stringRedisTemplate.opsForStream().createGroup(config.queueName(), config.consumerGroup());
    } catch (Exception ignored) {
    }
  }

  public void ack(ObjectRecord<String, BaseRedisMessage> record) {
    stringRedisTemplate.opsForStream().acknowledge(config.consumerGroup(), record);
  }
}
