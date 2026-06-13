package tutorials4j.framework.examples.signature;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("signature/simple")
  public String simple() {
    return "signature/simple";
  }
}
