package tutorials4j.framework.examples.message.redis.zset;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.redis.bean.DelayRedisMessage;
import tutorials4j.framework.message.redis.template.ZSetMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TaskHandler implements Runnable, Consumer<DelayRedisMessage> {
  private AtomicInteger id = new AtomicInteger(0);

  private final ZSetMessageTemplate taskTemplate;
  private final JacksonRecord jacksonRecord;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("task-handler=" + id.incrementAndGet());
    thread.start();
  }

  @Override
  public void run() {
    taskTemplate.consumerProcess(this);
  }

  @Override
  public void accept(DelayRedisMessage message) {
    if (ThreadLocalRandom.current().nextInt(99) < 60) {
      throw new RuntimeException("业务处理异常");
    }

    long milli = ThreadLocalRandom.current().nextInt(5000);
    try {
      TimeUnit.MILLISECONDS.sleep(milli);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    log.info("任务完成：{}", jacksonRecord.toObject(message.baseMessage().data(), TaskData.class));
  }
}
