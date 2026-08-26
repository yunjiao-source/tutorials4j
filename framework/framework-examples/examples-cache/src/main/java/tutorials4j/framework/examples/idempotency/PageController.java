package tutorials4j.framework.examples.idempotency;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
  @GetMapping("/order")
  public String order() {
    return "idempotency/order";
  }
}
