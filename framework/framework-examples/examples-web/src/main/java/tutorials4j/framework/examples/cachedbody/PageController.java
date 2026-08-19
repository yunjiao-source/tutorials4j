package tutorials4j.framework.examples.cachedbody;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 请求体缓存示例的页面控制器。
 *
 * <p>提供访问 {@code cache-body/simple} 页面的入口，用于演示请求体缓存功能。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /**
   * 返回请求体缓存示例页面视图。
   *
   * @return 视图名称 {@code cache-body/simple}
   */
  @GetMapping("simple")
  public String cacheBody() {
    return "cache-body/simple";
  }
}
