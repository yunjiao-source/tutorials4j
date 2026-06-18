package tutorials4j.framework.examples.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面视图控制器，用于展示缓存测试界面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("data")
  public String data() {
    // 返回 templates 目录下的 cache-demo.html
    return "simple/data";
  }
}
