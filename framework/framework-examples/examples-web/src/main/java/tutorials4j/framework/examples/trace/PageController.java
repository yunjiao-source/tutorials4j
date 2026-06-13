package tutorials4j.framework.examples.trace;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("trace/simple")
  public String simple() {
    return "trace/simple";
  }
}
