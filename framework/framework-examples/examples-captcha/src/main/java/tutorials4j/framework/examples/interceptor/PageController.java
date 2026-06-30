package tutorials4j.framework.examples.interceptor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("interceptor")
  public String interceptor() {
    return "interceptor/interceptor";
  }
}
