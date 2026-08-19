package tutorials4j.framework.examples.uid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UID 示例页面控制器。
 *
 * <p>提供 UID 生成示例的前端页面入口，返回名为 {@code uid/demo} 的视图。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 返回 UID 示例页面。
   *
   * @return 视图名称 {@code uid/demo}
   */
  @GetMapping("demo")
  public String demo() {
    return "uid/demo";
  }
}
