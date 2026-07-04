package tutorials4j.framework.examples.message.redis.list;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("send")
  public String send() {
    smsService.sendSms();
    return "ok";
  }
}
