package tutorials4j.framework.examples.multi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 多级缓存示例页面视图控制器。
 *
 * <p>负责渲染多级缓存测试界面，用于在浏览器中直观演示缓存效果。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 返回多级缓存示例页面视图。
   *
   * @return 视图名称 "multi/demo"
   */
  @GetMapping("/demo")
  public String demo() {
    return "multi/demo";
  }
}
