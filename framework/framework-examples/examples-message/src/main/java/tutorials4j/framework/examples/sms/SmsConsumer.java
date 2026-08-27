package tutorials4j.framework.examples.sms;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SmsConsumer {

  @Bean
  public Consumer<SmsMessage> sms() {
    return smsMessage -> {
      log.info(">>> 发送短信：{}", smsMessage);
    };
  }
}
