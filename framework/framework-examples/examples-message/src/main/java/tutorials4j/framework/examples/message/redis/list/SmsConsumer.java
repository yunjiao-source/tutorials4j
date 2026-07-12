package tutorials4j.framework.examples.message.redis.list;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.examples.message.redis.event.RedisMessageEvent;
import tutorials4j.framework.message.redis.bean.RedisMessage;
import tutorials4j.framework.message.redis.list.ListMessageTemplate;
import tutorials4j.framework.message.redis.template.RedisMessageConsumer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SmsConsumer implements Runnable, RedisMessageConsumer {
  private static final AtomicInteger id = new AtomicInteger(0);
  private final ApplicationEventPublisher publisher;
  private final JacksonRecord jacksonRecord;
  private final ListMessageTemplate smsTemplate;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("sms-consumer-" + id.incrementAndGet());
    thread.start();
  }

  @Override
  public void handleMessage(RedisMessage message) {
    SmsData data = jacksonRecord.toObject(message.getData(), SmsData.class);

    if (ThreadLocalRandom.current().nextInt(99) < 50) {
      throw new RuntimeException("业务处理异常");
    }

    long milli = ThreadLocalRandom.current().nextInt(5000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    publisher.publishEvent(new RedisMessageEvent("成功发送短信：" + data.id()));
  }

  @Override
  public void handleMessageWhenError(RedisMessage message, Throwable throwable) {
    SmsData data = jacksonRecord.toObject(message.getData(), SmsData.class);
    publisher.publishEvent(
        new RedisMessageEvent(
            "短信[id=" + data.id() + "]处理发生异常: " + message.getFailureReasons() + "，将消息存入数据库"));
  }

  @Override
  public void run() {
    smsTemplate.consumer(this);
  }
}
