package tutorials4j.framework.examples.signature;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器。
 *
 * <p>提供签名示例相关页面的视图跳转。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 跳转到签名示例的简单页面。
   *
   * @return 视图名称 {@code signature/simple}
   */
  @GetMapping("signature/simple")
  public String simple() {
    return "signature/simple";
  }
}
