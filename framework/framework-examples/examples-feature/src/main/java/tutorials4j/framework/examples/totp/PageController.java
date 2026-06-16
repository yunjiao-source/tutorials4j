package tutorials4j.framework.examples.totp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("2fa")
  public String a2fa() {
    return "totp/2fa";
  }

  @GetMapping("blog-2fa")
  public String blog2fa() {
    return "totp/blog-2fa";
  }
}
