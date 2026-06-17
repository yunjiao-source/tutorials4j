package tutorials4j.framework.examples.requestlogging;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("simple")
  public String simple() {
    return "requestlogging/simple";
  }
}
