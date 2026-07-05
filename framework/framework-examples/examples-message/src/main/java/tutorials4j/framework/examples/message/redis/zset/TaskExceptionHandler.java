package tutorials4j.framework.examples.message.redis.zset;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tutorials4j.framework.message.redis.bean.DelayRedisMessage;
import tutorials4j.framework.message.redis.template.ZSetMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@RequiredArgsConstructor
public class TaskExceptionHandler implements Runnable, Consumer<DelayRedisMessage> {
  private final ZSetMessageTemplate taskTemplate;

  public void start() {
    Thread thread = new Thread(this);
    thread.setName("task-exception-handler");
    thread.start();
  }

  @Override
  public void run() {
    taskTemplate.consumerDeadLetter(this);
  }

  @Override
  public void accept(DelayRedisMessage message) {
    if (message.baseMessage().retryCount() >= 3) {
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
    taskTemplate.addTask(message.cloneAndIncreaseRetryCount());
  }
}
