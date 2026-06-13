package tutorials4j.framework.examples.googleauth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("googleauth/2fa")
  public String a2fa() {
    return "googleauth/2fa";
  }

  @GetMapping("googleauth/blog-2fa")
  public String blog2fa() {
    return "googleauth/blog-2fa";
  }
}
