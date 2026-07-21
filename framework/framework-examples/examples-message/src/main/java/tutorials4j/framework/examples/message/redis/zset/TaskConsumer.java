package tutorials4j.framework.examples.message.redis.zset;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.examples.message.redis.event.RedisMessageEvent;
import tutorials4j.framework.message.redis.support.bean.RedisMessage;
import tutorials4j.framework.message.redis.support.template.RedisMessageConsumer;
import tutorials4j.framework.message.redis.support.zset.ZSetMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TaskConsumer implements Runnable, RedisMessageConsumer {
  private static final AtomicInteger id = new AtomicInteger(0);
  private final ApplicationEventPublisher publisher;
  private final JacksonRecord jacksonRecord;
  private final ZSetMessageTemplate taskTemplate;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("task-consumer-" + id.incrementAndGet());
    thread.start();
  }

  @Override
  public void handleMessage(RedisMessage message) {
    TaskData data = jacksonRecord.toObject(message.getData(), TaskData.class);

    if (ThreadLocalRandom.current().nextInt(99) < 30) {
      throw new RuntimeException("业务处理异常");
    }

    long milli = ThreadLocalRandom.current().nextInt(5000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    publisher.publishEvent(new RedisMessageEvent("延时任务处理完成：" + data.id()));
  }

  @Override
  public void handleMessageWhenError(RedisMessage message, Throwable throwable) {
    TaskData data = jacksonRecord.toObject(message.getData(), TaskData.class);
    publisher.publishEvent(
        new RedisMessageEvent(
            "延时任务[id=" + data.id() + "]处理发生异常: " + message.getFailureReasons() + "，将消息存入数据库"));
  }

  @Override
  public void run() {
    taskTemplate.consumer(this);
  }
}
