package tutorials4j.framework.examples.trace;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Trace（链路追踪）示例的页面视图控制器。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /** 返回 Trace 演示页面视图。 */
  @GetMapping("trace/simple")
  public String simple() {
    return "trace/simple";
  }
}
