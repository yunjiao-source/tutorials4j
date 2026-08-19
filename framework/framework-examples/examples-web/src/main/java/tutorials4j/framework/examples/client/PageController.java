package tutorials4j.framework.examples.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 示例页面控制器：提供展示三种 HTTP 客户端（RestTemplate、RestClient、WebClient）调用示例的页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 返回“三种客户端”示例页面。
   *
   * @return 页面模板路径 client/three-client
   */
  @GetMapping("client/three-client")
  public String threeClient() {
    return "client/three-client";
  }
}
