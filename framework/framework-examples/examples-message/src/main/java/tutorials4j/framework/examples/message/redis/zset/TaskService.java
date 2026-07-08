package tutorials4j.framework.examples.message.redis.zset;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;
import tutorials4j.framework.examples.message.redis.list.SmsData;
import tutorials4j.framework.message.redis.zset.ZSetMessageFactory;
import tutorials4j.framework.message.redis.zset.ZSetMessageHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class TaskService {
  public static final String MESSAGE_KEY = "task1";
  private static final AtomicLong id = new AtomicLong();
  private final ZSetMessageHandler taskHandler;
  private final TaskConsumer taskConsumer;

  private final JacksonRecord jacksonRecord;

  public TaskService(ZSetMessageFactory factory, ObjectMapperCreator creator) {
    this.taskHandler = factory.template(MESSAGE_KEY);

    jacksonRecord = new JacksonRecord(creator.getInstance());
    taskConsumer = new TaskConsumer(jacksonRecord, taskHandler);
  }

  public void addTask() {
    SmsData data = new SmsData(id.incrementAndGet(), Instant.now());
    long milli = ThreadLocalRandom.current().nextInt(10000);
    log.info("生成任务信息：{}, 延时：{}", data, milli);
    taskHandler.addTask(jacksonRecord.toJson(data), Duration.ofMillis(milli));
  }

  public void init() {
    // 开启两个消费线程
    taskConsumer.start();
    taskConsumer.start();
  }

  @PreDestroy
  public void destroy() {
    log.info("任务服务关闭");
    taskHandler.shutdown();
  }
}
