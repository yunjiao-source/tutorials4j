package tutorials4j.framework.examples.message.redis.list;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RequestMapping("/sms")
@RestController
@RequiredArgsConstructor
public class SmsController {
  private final SmsService smsService;

  @PostMapping("send")
  public void send() {
    IntStream.range(0, 5).forEach((i) -> smsService.sendSms());
  }
}
