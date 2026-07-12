package tutorials4j.framework.examples.message.redis.stream;

import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.redis.bean.RedisMessage;
import tutorials4j.framework.message.redis.stream.StreamMessageTempalteFactory;
import tutorials4j.framework.message.redis.stream.StreamMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class EmailService {
  private static final AtomicLong id = new AtomicLong();
  private final StreamMessageTemplate emailTemplate;
  private final EmailConsumer emailConsumer0;
  private final EmailConsumer emailConsumer1;

  private final JacksonRecord jacksonRecord;

  public EmailService(StreamMessageTempalteFactory factory, ObjectMapperCreator creator) {
    this.emailTemplate = factory.template(MessageConsts.MESSAGE_KEY_EMAIL);

    jacksonRecord = new JacksonRecord(creator.getInstance());
    emailConsumer0 = new EmailConsumer(jacksonRecord, emailTemplate, "group", "instance-0");
    emailConsumer1 = new EmailConsumer(jacksonRecord, emailTemplate, "group", "instance-1");
  }

  public void send() {
    EmailData data = new EmailData(id.incrementAndGet(), Instant.now());
    log.info("生成Email信息：{}", data);

    RedisMessage message = RedisMessage.defaultValue().setData(jacksonRecord.toJson(data));
    emailTemplate.send(message);
  }

  public void init() {
    // 开启两个消费线程
    Thread thread0 = new Thread(emailConsumer0);
    thread0.setName("email-consumer-0");
    thread0.start();

    Thread thread1 = new Thread(emailConsumer1);
    thread1.setName("email-consumer-1");
    thread1.start();
  }

  @PreDestroy
  public void destroy() {
    log.info("Email服务关闭");
    emailTemplate.shutdown();
  }

  @Scheduled(initialDelay = 3000, fixedDelay = 5000)
  public void clean() {
    long count = emailTemplate.trimByMinId();
    if (count > 0) {
      log.info("清理了{}条记录", count);
    }
    return;
  }
}
