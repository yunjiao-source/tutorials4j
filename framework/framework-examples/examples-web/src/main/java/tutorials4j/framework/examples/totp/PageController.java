package tutorials4j.framework.examples.totp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TOTP 双因素认证示例的页面视图控制器。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  /** 返回双因素认证演示页面视图。 */
  @GetMapping("2fa")
  public String a2fa() {
    return "totp/2fa";
  }

  /** 返回带 TOTP 认证的博客发布演示页面视图。 */
  @GetMapping("blog-2fa")
  public String blog2fa() {
    return "totp/blog-2fa";
  }
}
