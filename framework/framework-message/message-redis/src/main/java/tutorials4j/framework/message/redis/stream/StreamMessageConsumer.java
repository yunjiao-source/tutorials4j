package tutorials4j.framework.message.redis.stream;

import org.springframework.data.redis.connection.stream.ObjectRecord;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.properties.StreamQueueOptions;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface StreamMessageConsumer {
  void handleMessage(ObjectRecord<String, BaseRedisMessage> message);

  void setQueueName(String queueName);

  void setStreamQueueOptions(StreamQueueOptions options);
}
