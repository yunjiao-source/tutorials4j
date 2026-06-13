package tutorials4j.framework.examples.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("client/three-client")
  public String threeClient() {
    return "client/three-client";
  }
}
