package tutorials4j.framework.message.redis.template;

import tutorials4j.framework.message.redis.bean.RedisMessageType;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface RedisMessageTemplateFactory<T> {
  T template(String key);

  RedisMessageType getMessageType();
}
