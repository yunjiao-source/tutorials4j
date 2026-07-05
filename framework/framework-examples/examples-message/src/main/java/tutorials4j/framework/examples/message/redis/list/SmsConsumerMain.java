package tutorials4j.framework.examples.message.redis.list;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.redis.bean.BaseRedisMessage;
import tutorials4j.framework.message.redis.template.ListMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SmsConsumerMain implements Runnable, Function<BaseRedisMessage, Boolean> {
  private AtomicInteger id = new AtomicInteger(0);
  private final ListMessageTemplate smsTemplate;
  private final JacksonRecord jacksonRecord;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("sms-consumer-main-" + id.incrementAndGet());
    thread.start();
  }

  @Override
  public void run() {
    smsTemplate.consumerMain(this);
  }

  @Override
  public Boolean apply(BaseRedisMessage message) {
    if (ThreadLocalRandom.current().nextInt(99) < 40) {
      throw new RuntimeException("业务处理异常");
    }

    if (ThreadLocalRandom.current().nextInt(99) < 40) {
      return false;
    }

    long milli = ThreadLocalRandom.current().nextInt(5000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    log.info("发送短信：{}", jacksonRecord.toObject(message.data(), SmsData.class));
    return true;
  }
}
