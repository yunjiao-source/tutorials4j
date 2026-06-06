package tutorials4j.framework.examples.app;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主页面
 *
 * @author Yun Jiao
 */
public class IndexController {
  @GetMapping
  public String index() {
    return "index";
  }
}
