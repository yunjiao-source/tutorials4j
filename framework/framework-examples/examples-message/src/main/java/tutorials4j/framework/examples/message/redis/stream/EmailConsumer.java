package tutorials4j.framework.examples.message.redis.stream;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.examples.message.redis.list.SmsData;
import tutorials4j.framework.message.redis.support.bean.RedisMessage;
import tutorials4j.framework.message.redis.support.stream.StreamMessageTemplate;
import tutorials4j.framework.message.redis.support.template.RedisMessageConsumer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class EmailConsumer implements Runnable, RedisMessageConsumer {
  private static final AtomicInteger id = new AtomicInteger(0);
  private final JacksonRecord jacksonRecord;
  private final StreamMessageTemplate emailTemplate;
  private final String consumerGroup;
  private final String consumerName;

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

    log.info("处理任务完成：{}", data);
  }

  @Override
  public void handleMessageWhenError(RedisMessage message, Throwable throwable) {
    log.error("任务处理发生异常[{}]，将消息存入数据库[{}]", throwable.getMessage(), message.getId());
  }

  @Override
  public void run() {
    emailTemplate.consumer(consumerGroup, consumerName, this);
  }
}
