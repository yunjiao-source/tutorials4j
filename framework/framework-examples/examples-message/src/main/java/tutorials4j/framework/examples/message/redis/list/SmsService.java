package tutorials4j.framework.examples.message.redis.list;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tutorials4j.framework.common.spring.jackson.JacksonRecord;
import tutorials4j.framework.common.spring.jackson.ObjectMapperCreator;
import tutorials4j.framework.message.redis.bean.MessageConsts;
import tutorials4j.framework.message.redis.factory.ListMessageFactory;
import tutorials4j.framework.message.redis.template.ListMessageTemplate;

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
  private final SmsConsumerMain smsConsumerMain;
  private final SmsConsumerDeadLetter smsConsumerDeadLetter;
  private final JacksonRecord jacksonRecord;

  public SmsService(ListMessageFactory factory, ObjectMapperCreator creator) {
    this.smsTemplate = factory.template(MessageConsts.MESSAGE_KEY_SMS);

    jacksonRecord = new JacksonRecord(creator.getInstance());
    smsConsumerDeadLetter = new SmsConsumerDeadLetter(smsTemplate, jacksonRecord);
    smsConsumerMain = new SmsConsumerMain(smsTemplate, jacksonRecord);
  }

  public void sendSms() {
    SmsData data = new SmsData(id.incrementAndGet(), Instant.now());
    log.info("生成短信消息：{}", data);
    smsTemplate.send(jacksonRecord.toJson(data));
  }

  public void init() {
    // 开启两个线程
    smsConsumerMain.start();
    smsConsumerMain.start();

    smsConsumerDeadLetter.start();
  }
}
