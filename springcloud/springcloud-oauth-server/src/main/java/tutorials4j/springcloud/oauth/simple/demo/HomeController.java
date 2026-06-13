// HomeController.java
package tutorials4j.springcloud.oauth.simple.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping()
  public String index() {
    return "simple/index"; // 对应 src/main/resources/templates/index.html
  }
}
