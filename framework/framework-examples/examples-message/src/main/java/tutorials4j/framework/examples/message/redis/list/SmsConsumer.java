package tutorials4j.framework.examples.message.redis.list;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.list.ListMessageHandler;
import tutorials4j.framework.message.redis.list.ListRedisMessageConsumer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SmsConsumer implements Runnable, ListRedisMessageConsumer {
  private static final AtomicInteger id = new AtomicInteger(0);
  private final JacksonRecord jacksonRecord;
  private final ListMessageHandler smsHandler;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("sms-consumer-main-" + id.incrementAndGet());
    thread.start();
  }

  @Override
  public void handleMessage(BaseRedisMessage message) {
    SmsData data = jacksonRecord.toObject(message.getData(), SmsData.class);

    if (ThreadLocalRandom.current().nextInt(99) < 30) {
      throw new RuntimeException("业务处理异常");
    }

    long milli = ThreadLocalRandom.current().nextInt(5000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    log.info("发送短信：{}", data);
  }

  @Override
  public void handleMessageWhenError(BaseRedisMessage message, Throwable throwable) {
    log.error("短信处理发生异常[{}]，将消息存入数据库[{}]", throwable.getMessage(), message.getId());
  }

  @Override
  public void run() {
    smsHandler.consumer(this);
  }
}
