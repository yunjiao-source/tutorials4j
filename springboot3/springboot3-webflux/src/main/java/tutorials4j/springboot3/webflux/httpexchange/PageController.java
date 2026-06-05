package tutorials4j.springboot3.webflux.httpexchange; // src/main/java/com/example/demo/controller/PageController.java

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

  @GetMapping("/httpexchange")
  public String httpExchange() {
    return "httpexchange";
  }
}
