package tutorials4j.framework.examples.message.redis.list;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.message.redis.bean.RedisMessage;
import tutorials4j.framework.message.redis.template.ListMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class SmsConsumerMain implements Runnable, Function<RedisMessage, Boolean> {
  private final ListMessageTemplate smsTemplate;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("sms-consumer-main");
    thread.start();
  }

  @Override
  public void run() {
    smsTemplate.consumerMain(this);
  }

  @Override
  public Boolean apply(RedisMessage message) {
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

    log.info("发送短信：{}", message);
    return true;
  }
}
