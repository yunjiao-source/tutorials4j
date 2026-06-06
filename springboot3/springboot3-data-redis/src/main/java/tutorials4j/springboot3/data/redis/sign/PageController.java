package tutorials4j.springboot3.data.redis.sign; // src/main/java/com/example/demo/controller/PageController.java

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/sign")
  public String sign() {
    return "sign";
  }
}
