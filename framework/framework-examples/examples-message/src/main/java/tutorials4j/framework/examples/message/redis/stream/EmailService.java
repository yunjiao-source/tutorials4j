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
import tutorials4j.framework.message.redis.stream.StreamMessageHandler;
import tutorials4j.framework.message.redis.stream.StreamMessageHandlerFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class EmailService {
  private static final AtomicLong id = new AtomicLong();
  private final StreamMessageHandler emailHandler;
  private final EmailConsumer emailConsumer;

  private final JacksonRecord jacksonRecord;

  public EmailService(StreamMessageHandlerFactory factory, ObjectMapperCreator creator) {
    this.emailHandler = factory.handler(MessageConsts.MESSAGE_KEY_EMAIL);

    jacksonRecord = new JacksonRecord(creator.getInstance());
    emailConsumer = new EmailConsumer(jacksonRecord, emailHandler);
  }

  public void send() {
    EmailData data = new EmailData(id.incrementAndGet(), Instant.now());
    log.info("生成Email信息：{}", data);
    emailHandler.send(jacksonRecord.toJson(data));
  }

  public void init() {
    // 开启两个消费线程
    emailConsumer.start();
    emailConsumer.start();
  }

  @PreDestroy
  public void destroy() {
    log.info("Email服务关闭");
    emailHandler.shutdown();
  }

  @Scheduled(initialDelay = 3000, fixedDelay = 5000)
  public void clean() {
    long count = emailHandler.trimByMinId();
    if (count > 0) {
      log.info("清理了{}条记录", count);
    }
  }
}
