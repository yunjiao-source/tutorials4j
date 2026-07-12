package tutorials4j.framework.examples.message.redis.integration.sub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.message.redis.bean.RedisMessage;
import tutorials4j.framework.message.redis.template.RedisMessageConsumer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class DemoRedisConsumer implements RedisMessageConsumer {

  @Override
  public void handleMessage(RedisMessage message) {}

  @Override
  public void handleMessageWhenError(RedisMessage message, Throwable throwable) {}

  //  private static final AtomicInteger id = new AtomicInteger(0);
  //  private static final int MAX_RETRY_COUNT = 3;
  //  // 有效期
  //  private static final Duration expireTime = Duration.ofSeconds(30);
  //  private final DemoService demoService;
  //  private final JacksonRecord jacksonRecord;
  //  private final RedisMessageTemplate demoStreamHandler;
  //  private final RedisMessageTemplate demoZSetHandler;
  //
  //  public void start() {
  //    Thread thread = new Thread(() -> demoStreamHandler.consumer(this));
  //    thread.setName("demo-stream-" + id.incrementAndGet());
  //    thread.start();
  //
  //    Thread thread2 = new Thread(() -> demoZSetHandler.consumer(this));
  //    thread2.setName("demo-delay-" + id.incrementAndGet());
  //    thread2.start();
  //  }
  //
  //  @Override
  //  public String getConsumerName() {
  //    return "instance-0";
  //  }
  //
  //  @Override
  //  public void handleMessage(RedisMessage message) {
  //    DemoData data = jacksonRecord.toObject(message.getData(), DemoData.class);
  //    demoService.handle(data);
  //  }
  //
  //  @Override
  //  public void handleMessageWhenError(RedisMessage message, Throwable throwable) {
  //    message.addReason(throwable.getMessage());
  //    DemoData data = jacksonRecord.toObject(message.getData(), DemoData.class);
  //
  //    if (message.getRetryCount() >= MAX_RETRY_COUNT) {
  //      message.addReason("超过最大重试次数:" + MAX_RETRY_COUNT);
  //      demoService.handleError(data, message);
  //      return;
  //    }
  //
  //    Duration elapsed = Duration.between(message.getTimestamp(), Instant.now());
  //    if (elapsed.compareTo(expireTime) > 0) {
  //      message.addReason("数据过期:" + expireTime);
  //      demoService.handleError(data, message);
  //      return;
  //    }
  //
  //    if (message.getDelayTime() == null) {
  //      // 默认延时5秒
  //      message.setDelayTime(Duration.ofSeconds(5));
  //    }
  //    log.info("发送延时队列，等等再次处理:{}", data.id());
  //    message.increaseRetryCount();
  //    demoZSetHandler.send(message);
  //  }
}
