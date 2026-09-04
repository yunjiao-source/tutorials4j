package tutorials4j.framework.examples.apikey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("demo")
  public String demo() {
    return "apikey/demo";
  }
}
