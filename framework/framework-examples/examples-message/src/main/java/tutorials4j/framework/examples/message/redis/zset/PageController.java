package tutorials4j.framework.examples.message.redis.zset;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
  @GetMapping("/task")
  public String smsPage() {
    return "redis/zset";
  }
}
