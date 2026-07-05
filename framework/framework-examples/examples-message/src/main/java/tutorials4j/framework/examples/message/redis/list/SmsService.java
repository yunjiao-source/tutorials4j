package tutorials4j.framework.examples.message.redis.list;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

  public SmsService(ListMessageFactory factory) {
    this.smsTemplate = factory.template(MessageConsts.MESSAGE_KEY_SMS);

    smsConsumerDeadLetter = new SmsConsumerDeadLetter(smsTemplate);
    smsConsumerMain = new SmsConsumerMain(smsTemplate);
  }

  public void sendSms() {
    Map<String, String> data =
        Map.of("id", "" + id.incrementAndGet(), "Date", Instant.now().toString());
    log.info("生成短信消息：{}", data);
    smsTemplate.send(data);
  }

  public void init() {
    // 开启两个线程
    smsConsumerMain.start();
    smsConsumerMain.start();

    smsConsumerDeadLetter.start();
  }
}
