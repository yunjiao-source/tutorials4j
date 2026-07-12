package tutorials4j.framework.examples.message.redis.list;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
  @GetMapping("/sms")
  public String smsPage() {
    return "redis/list";
  }
}
