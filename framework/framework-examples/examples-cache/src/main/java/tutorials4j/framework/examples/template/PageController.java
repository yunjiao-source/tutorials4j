package tutorials4j.framework.examples.template;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面视图控制器，用于展示缓存测试界面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("/demo")
  public String demo() {
    return "template/demo";
  }
}
