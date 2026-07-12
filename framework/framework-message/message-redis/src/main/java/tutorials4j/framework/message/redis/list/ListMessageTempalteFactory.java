package tutorials4j.framework.message.redis.list;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.redis.bean.RedisMessageType;
import tutorials4j.framework.message.redis.properties.QueueOptions;
import tutorials4j.framework.message.redis.template.AbstractRedisMessageTempalteFactory;
import tutorials4j.framework.message.redis.template.TemplateConfig;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
public class ListMessageTempalteFactory
    extends AbstractRedisMessageTempalteFactory<ListMessageTemplate> {
  public static final ListMessageTempalteFactory instance = new ListMessageTempalteFactory();

  @Override
  protected ListMessageTemplate createTemplate(
      StringRedisTemplate stringRedisTemplate,
      JacksonRecord jacksonRecord,
      QueueOptions options,
      String name) {

    String queueName = MessageConsts.getMessageQueue(getMessageType().name(), name);
    TemplateConfig config = TemplateConfig.builder().name(name).queueName(queueName).build();
    config.validate();

    return new ListMessageTemplate(stringRedisTemplate, jacksonRecord, config, options);
  }

  @Override
  public RedisMessageType getMessageType() {
    return RedisMessageType.list;
  }
}
