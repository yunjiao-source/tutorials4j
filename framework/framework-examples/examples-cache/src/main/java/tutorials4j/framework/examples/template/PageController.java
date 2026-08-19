package tutorials4j.framework.examples.template;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 缓存模板示例页面视图控制器。
 *
 * <p>负责渲染缓存模板演示页面，用于在浏览器中展示验证码缓存与计数器缓存的使用效果。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 展示缓存测试演示页面。
   *
   * @return 视图名称 template/demo
   */
  @GetMapping("/demo")
  public String demo() {
    return "template/demo";
  }
}
