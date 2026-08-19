package tutorials4j.framework.examples.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 验证码示例页面控制器。
 *
 * <p>提供验证码示例的前端页面入口，返回名为 {@code simple/data} 的视图。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 返回验证码示例页面。
   *
   * @return 视图名称 {@code simple/data}
   */
  @GetMapping("data")
  public String data() {
    // 返回 templates 目录下的 cache-demo.html
    return "simple/data";
  }
}
