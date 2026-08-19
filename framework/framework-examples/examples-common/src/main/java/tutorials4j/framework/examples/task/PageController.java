package tutorials4j.framework.examples.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 组合任务装饰器示例的页面视图控制器。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /** 返回任务示例演示页面视图。 */
  @GetMapping("demo")
  public String demo() {
    return "task/demo";
  }
}
