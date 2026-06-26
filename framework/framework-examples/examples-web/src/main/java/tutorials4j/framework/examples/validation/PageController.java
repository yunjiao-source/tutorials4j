package tutorials4j.framework.examples.validation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("demo")
  public String demo() {
    return "validation/demo";
  }

  @GetMapping("register")
  public String register() {
    return "validation/register";
  }
}
