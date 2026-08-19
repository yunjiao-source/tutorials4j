// HomeController.java
package tutorials4j.springcloud.oauth.simple.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器，渲染接口文档首页。
 *
 * @author Yun Jiao
 */
@Controller
public class HomeController {

  /**
   * 渲染接口文档首页视图。
   *
   * @return 视图名，对应模板 simple/index
   */
  @GetMapping()
  public String index() {
    return "simple/index"; // 对应 src/main/resources/templates/index.html
  }
}
