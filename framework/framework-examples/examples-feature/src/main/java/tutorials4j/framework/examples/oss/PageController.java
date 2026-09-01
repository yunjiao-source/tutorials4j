package tutorials4j.framework.examples.oss;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 签到功能页面控制器，提供签到录入与签到列表页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("query")
  public String page() {
    return "oss/query";
  }

  @GetMapping("upload")
  public String upload() {
    return "oss/upload";
  }
}
