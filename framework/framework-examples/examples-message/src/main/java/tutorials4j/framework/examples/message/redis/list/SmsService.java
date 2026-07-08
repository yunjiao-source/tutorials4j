package tutorials4j.framework.examples.message.redis.list;

import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.redis.list.ListMessageFactory;
import tutorials4j.framework.message.redis.list.ListMessageHandler;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class SmsService {
  private final AtomicLong id = new AtomicLong();
  private final ListMessageHandler smsHandler;
  private final SmsConsumer smsConsumer;
  private final JacksonRecord jacksonRecord;

  public SmsService(ListMessageFactory factory, ObjectMapperCreator creator) {
    this.smsHandler = factory.template(MessageConsts.MESSAGE_KEY_SMS);

    jacksonRecord = new JacksonRecord(creator.getInstance());
    smsConsumer = new SmsConsumer(jacksonRecord, smsHandler);
  }

  public void sendSms() {
    SmsData data = new SmsData(id.incrementAndGet(), Instant.now());
    log.info("生成短信消息：{}", data);
    smsHandler.send(jacksonRecord.toJson(data));
  }

  public void init() {
    // 开启两个线程
    smsConsumer.start();
    smsConsumer.start();
  }

  @PreDestroy
  public void destroy() {
    log.info("短信服务关闭");
    smsHandler.shutdown();
  }
}
