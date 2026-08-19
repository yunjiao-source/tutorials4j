package tutorials4j.framework.examples.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 加密示例页面控制器。
 *
 * <p>提供加密传输示例的前端页面入口，返回名为 {@code api} 的视图。
 *
 * @author Yun Jiao
 */
@Controller
public class ViewController {
  /**
   * 返回加密示例页面。
   *
   * @return 视图名称 {@code api}
   */
  @GetMapping("api")
  public String api() {
    return "api";
  }
}
