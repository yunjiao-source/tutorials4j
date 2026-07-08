package tutorials4j.framework.message.redis.stream;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.properties.StreamQueueOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstracStreamMessageConsumer implements StreamMessageConsumer {
  public abstract void handleMessage(BaseRedisMessage message);

  protected final StringRedisTemplate redisTemplate;
  protected String queueName;
  protected StreamQueueOptions options;

  @Override
  public void handleMessage(ObjectRecord<String, BaseRedisMessage> record) {
    if (StringUtils.isBlank(queueName) || options == null) {
      throw new IllegalStateException(
          "Redis Stream 消息处理器配置问题，queueName=" + queueName + ", options=" + options);
    }

    String idValue = record.getId().getValue();
    boolean idempotent = options.getIdempotent().isEnabled();
    String idemKey = MessageConsts.getMessageQueueIdempotent(queueName) + ":" + idValue;
    // 幂等校验
    if (idempotent) {
      Boolean firstConsume =
          redisTemplate
              .opsForValue()
              .setIfAbsent(
                  idemKey,
                  "1",
                  options.getIdempotent().getExpiredTime().toSeconds(),
                  TimeUnit.SECONDS);
      if (!Boolean.TRUE.equals(firstConsume)) {
        // 已消费过，直接ack丢弃消息
        ackMsg(record);
        if (log.isDebugEnabled()) {
          log.debug("消息重复消费：queueName={}, record={}", queueName, record);
        }
        return;
      }
    }

    try {
      BaseRedisMessage message = record.getValue();
      handleMessage(message);
      ackMsg(record);
    } catch (Exception e) {
      if (idempotent) {
        redisTemplate.delete(idemKey);
      }
      throw e;
    }
  }

  // 确认消息
  private void ackMsg(ObjectRecord<String, BaseRedisMessage> record) {
    if (!options.isAutoAck()) {
      String streamKey = record.getStream();
      String idValue = record.getId().getValue();
      redisTemplate.opsForStream().acknowledge(streamKey, options.getConsumerGroup(), idValue);
    }
  }

  @Override
  public void setQueueName(String queueName) {
    this.queueName = queueName;
  }

  @Override
  public void setStreamQueueOptions(StreamQueueOptions options) {
    this.options = options;
  }
}
