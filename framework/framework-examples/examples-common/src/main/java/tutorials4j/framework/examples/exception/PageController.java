package tutorials4j.framework.examples.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("demo")
  public String demo() {
    return "exception/demo";
  }
}
