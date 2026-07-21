package tutorials4j.framework.examples.message.redis.list;

import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.message.core.bean.MessageConsts;
import tutorials4j.framework.message.redis.support.list.ListMessageTempalteFactory;
import tutorials4j.framework.message.redis.support.list.ListMessageTemplate;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Slf4j
@Component
public class SmsService {
  private final AtomicLong id = new AtomicLong();
  private final ListMessageTemplate smsTemplate;
  private final SmsConsumer smsConsumer;
  private final JacksonRecord jacksonRecord;

  public SmsService(
      ListMessageTempalteFactory factory,
      JacksonRecord jacksonRecord,
      ApplicationEventPublisher publisher) {
    this.jacksonRecord = jacksonRecord;
    this.smsTemplate = factory.template(MessageConsts.MESSAGE_KEY_SMS);

    smsConsumer = new SmsConsumer(publisher, jacksonRecord, smsTemplate);
  }

  public void sendSms() {
    SmsData data = new SmsData(id.incrementAndGet(), Instant.now());
    log.info("生成短信消息：{}", data);

    smsTemplate.send(jacksonRecord.toJson(data));
  }

  public void init() {
    // 开启两个线程
    smsConsumer.start();
    smsConsumer.start();
  }

  @PreDestroy
  public void destroy() {
    log.info("短信服务关闭");
    smsTemplate.shutdown();
  }
}
