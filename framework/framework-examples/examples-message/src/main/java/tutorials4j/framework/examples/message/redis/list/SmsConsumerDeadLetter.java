package tutorials4j.framework.examples.message.redis.list;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
public class SmsConsumerDeadLetter implements Runnable, Consumer<BaseRedisMessage> {
  private final ListMessageTemplate smsTemplate;
  private final JacksonRecord jacksonRecord;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("sms-consumer-dead_letter");
    thread.start();
  }

  @Override
  public void run() {
    smsTemplate.consumerDeadLetter(this);
  }

  @Override
  public void accept(BaseRedisMessage message) {
    if (message.retryCount() >= 3) {
      log.error("超过最大重试次数：{}", message);
      return;
    }

    // 等待
    long milli = ThreadLocalRandom.current().nextInt(3000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // 重新发送
    smsTemplate.send(message.cloneAndIncreaseRetryCount());
  }
}
