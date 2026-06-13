package tutorials4j.framework.examples.cache;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("spring-cache")
  public String springCache() {
    return "spring-cache";
  }
}
