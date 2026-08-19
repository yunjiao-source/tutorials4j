package tutorials4j.framework.examples.xss;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * XSS 安全示例的页面视图控制器。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /** 返回 XSS 演示页面视图。 */
  @GetMapping("simple")
  public String simple() {
    return "xss/simple";
  }
}
