package tutorials4j.framework.examples.cacheable;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Caffeine 缓存示例页面视图控制器。
 *
 * <p>负责渲染缓存测试演示页面，用于在浏览器中直观展示 Caffeine 缓存的效果。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 返回缓存测试页面视图。
   *
   * @return 视图名称 "cacheable/cache-demo"
   */
  @GetMapping("/cache-demo")
  public String cacheDemoPage() {
    // 返回 templates 目录下的 cache-demo.html
    return "cacheable/cache-demo";
  }
}
