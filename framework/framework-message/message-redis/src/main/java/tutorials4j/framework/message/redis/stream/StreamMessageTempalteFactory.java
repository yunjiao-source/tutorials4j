package tutorials4j.framework.message.redis.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.redis.bean.RedisMessageType;
import tutorials4j.framework.message.redis.properties.QueueOptions;
import tutorials4j.framework.message.redis.template.AbstractRedisMessageTempalteFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class StreamMessageTempalteFactory
    extends AbstractRedisMessageTempalteFactory<StreamMessageTemplate> {
  public static final StreamMessageTempalteFactory instance = new StreamMessageTempalteFactory();

  @Override
  protected StreamMessageTemplate createTemplate(
      StringRedisTemplate stringRedisTemplate,
      JacksonRecord jacksonRecord,
      QueueOptions options,
      String name) {
    StreamMessageConfig config =
        StreamMessageConfig.builder()
            .name(name)
            .queueName(MessageConsts.getMessageQueue(getMessageType().name(), name))
            .blockTimeout(options.getBlockTimeout())
            .sleepTimeWhenException(options.getSleepTimeWhenException())
            .build();
    config.validate();

    return new StreamMessageTemplate(stringRedisTemplate, config);
  }

  @Override
  public RedisMessageType getMessageType() {
    return RedisMessageType.stream;
  }
}
