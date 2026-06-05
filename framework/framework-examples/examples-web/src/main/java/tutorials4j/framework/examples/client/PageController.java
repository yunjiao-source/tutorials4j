package tutorials4j.framework.examples.client; // src/main/java/com/example/demo/controller/PageController.java

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/client")
  public String httpExchange() {
    return "client";
  }
}
