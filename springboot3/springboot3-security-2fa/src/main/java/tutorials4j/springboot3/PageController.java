package tutorials4j.springboot3;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("/2fa-setup")
  public String twoFactorSetupPage() {
    return "2fa-setup";
  }
}
