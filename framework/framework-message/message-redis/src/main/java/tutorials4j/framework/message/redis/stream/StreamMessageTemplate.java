package tutorials4j.framework.message.redis.stream;

import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.exception.MessageErrorCode;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class StreamMessageTemplate {
  private final StringRedisTemplate stringRedisTemplate;
  private final JacksonRecord jacksonRecord;
  private final StreamMessageConfig config;

  public StreamMessageTemplate(
      StringRedisTemplate stringRedisTemplate,
      JacksonRecord jacksonRecord,
      StreamMessageConfig config) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jacksonRecord = jacksonRecord;
    this.config = config;
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
            .add(StreamRecords.objectBacked(message).withStreamKey(config.getMainQueue()));
    return recordId != null ? recordId.getValue() : null;
  }

  public void consumerPending(String consumerGroup) {
    Assert.hasText(consumerGroup, "consumerGroup must not be null or empty");

    String streamKey = config.getMainQueue();
    PendingMessagesSummary summary =
        stringRedisTemplate.opsForStream().pending(streamKey, consumerGroup);
    if (summary == null || summary.getTotalPendingMessages() == 0) {
      return;
    }

    PendingMessages pendingMessages =
        stringRedisTemplate
            .opsForStream()
            .pending(streamKey, consumerGroup, Range.unbounded(), config.countPreRead());

    pendingMessages.forEach(
        pendingMessage -> {
          String messageId = pendingMessage.getId().getValue();
          long deliveryCount = pendingMessage.getTotalDeliveryCount();
          long idleTimeMillis = pendingMessage.getElapsedTimeSinceLastDelivery().toMillis();

          if (idleTimeMillis < config.pendingTimeout().toMillis()) {
            // 只处理超时的消息
            return;
          }

          if (deliveryCount >= config.maxRetryCount()) {
            moveToDeadLetterQueue(messageId, "Max retries exceeded");
            // 从 PEL 中删除 (XACK) 避免再次处理
            stringRedisTemplate
                .opsForStream()
                .acknowledge(streamKey, consumerGroup, pendingMessage.getId());
            return;
          }

          // 重新投递
          if (log.isDebugEnabled()) {
            log.info("消息准备进行第 {} 次重新投递", deliveryCount + 1);
          }
          stringRedisTemplate
              .opsForStream()
              .claim(
                  streamKey,
                  consumerGroup,
                  "retry-consumer",
                  config.claimMinIdleTime(),
                  pendingMessage.getId());
        });
  }

  private void moveToDeadLetterQueue(String messageId, String reason) {
    List<ObjectRecord<String, BaseRedisMessage>> records =
        stringRedisTemplate
            .opsForStream()
            .range(BaseRedisMessage.class, config.getMainQueue(), Range.just(messageId));
    if (records.isEmpty()) return;

    ObjectRecord<String, BaseRedisMessage> record = records.get(0);
    BaseRedisMessage originalMessage = record.getValue();
    BaseRedisMessage newMessage = originalMessage.clone();
    newMessage.setFailureReason(reason);

    String deadLetterQueue = config.getDeadLetterQueue();
    if (log.isDebugEnabled()) {
      log.debug("消息已转入死信队列, queueName={}, reason={}", deadLetterQueue, messageId);
    }
    stringRedisTemplate
        .opsForStream()
        .add(StreamRecords.objectBacked(newMessage).withStreamKey(deadLetterQueue));
  }

  private void createConsumerGroup(String groupName) {
    try {
      stringRedisTemplate.opsForStream().createGroup("", ReadOffset.from("0"), groupName);
    } catch (Exception e) {
      // 消费者组已存在，忽略异常
    }
  }
}
