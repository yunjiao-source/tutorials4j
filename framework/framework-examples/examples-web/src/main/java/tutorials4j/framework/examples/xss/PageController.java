package tutorials4j.framework.examples.xss;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("xss/simple")
  public String simple() {
    return "xss/simple";
  }
}
