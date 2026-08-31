package tutorials4j.framework.examples.fss;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("fss-demo")
  public String fssDemo() {
    return "fss/demo";
  }
}
