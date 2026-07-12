package tutorials4j.framework.message.redis.template;

import tutorials4j.framework.message.redis.bean.RedisMessage;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface RedisMessageConsumer {
  void handleMessage(RedisMessage message);

  void handleMessageWhenError(RedisMessage message, Throwable throwable);
}
