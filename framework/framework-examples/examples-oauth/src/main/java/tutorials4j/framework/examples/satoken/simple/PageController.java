package tutorials4j.framework.examples.satoken.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("simple")
  public String fssDemo() {
    return "sa-token/simple";
  }
}
