package tutorials4j.framework.examples.cache;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring Cache 缓存示例的页面视图控制器。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /** 返回 Spring Cache 演示页面视图。 */
  @GetMapping("spring-cache")
  public String springCache() {
    return "spring-cache";
  }
}
