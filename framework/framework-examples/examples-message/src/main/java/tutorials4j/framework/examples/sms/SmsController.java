package tutorials4j.framework.examples.sms;

import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("sms")
@RequiredArgsConstructor
public class SmsController {
  private AtomicLong counter = new AtomicLong(13000000000L);
  private final SmsProducer smsProducer;

  @PostMapping("/send")
  public String send(@RequestBody String message) {
    long phone = counter.incrementAndGet();
    smsProducer.sendSms("" + phone, message);
    return "短信发送成功: " + phone;
  }
}
