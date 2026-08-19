package tutorials4j.framework.examples.requestlogging;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 请求日志示例的页面控制器。
 *
 * <p>提供访问 {@code requestlogging/simple} 页面的入口，用于演示请求日志功能。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 返回请求日志示例页面视图。
   *
   * @return 视图名称 {@code requestlogging/simple}
   */
  @GetMapping("simple")
  public String simple() {
    return "requestlogging/simple";
  }
}
