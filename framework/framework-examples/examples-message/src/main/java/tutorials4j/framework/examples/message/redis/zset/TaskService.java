package tutorials4j.framework.examples.message.redis.zset;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.message.redis.factory.ZSetMessageFactory;
import tutorials4j.framework.message.redis.template.ZSetMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class TaskService {
  public static final String MESSAGE_KEY = "task1";
  private final AtomicLong id = new AtomicLong();
  private final ZSetMessageTemplate taskTemplate;
  private final TaskHandler taskHandler;
  private final TaskExceptionHandler taskExceptionHandler;

  public TaskService(ZSetMessageFactory factory) {
    this.taskTemplate = factory.template(MESSAGE_KEY);

    taskExceptionHandler = new TaskExceptionHandler(taskTemplate);
    taskHandler = new TaskHandler(taskTemplate);
  }

  public void addTask() {
    Map<String, String> data =
        Map.of("id", "" + id.incrementAndGet(), "Date", Instant.now().toString());
    log.info("生成任务信息：{}", data);

    long milli = ThreadLocalRandom.current().nextInt(10000);

    taskTemplate.addTask(data, Duration.ofMillis(milli));
  }

  public void init() {
    // 开启两个消费线程
    taskHandler.start();
    taskHandler.start();

    taskExceptionHandler.start();
  }
}
