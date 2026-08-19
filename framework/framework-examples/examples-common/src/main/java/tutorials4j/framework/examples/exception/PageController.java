package tutorials4j.framework.examples.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 异常示例页面控制器。
 *
 * <p>提供异常演示页面的视图跳转入口。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 跳转至异常演示页面。
   *
   * @return 视图名称 {@code exception/demo}
   */
  @GetMapping("demo")
  public String demo() {
    return "exception/demo";
  }
}
