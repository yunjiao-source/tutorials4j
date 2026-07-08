package tutorials4j.framework.message.redis.bean;

/**
 * TODO
 *
 * @author Yun Jiao
 */
public interface BaseRedisMessageConsumer<T extends BaseRedisMessage> {
  void handleMessage(BaseRedisMessage message);

  void handleMessageWhenError(BaseRedisMessage message, Throwable throwable);
}
