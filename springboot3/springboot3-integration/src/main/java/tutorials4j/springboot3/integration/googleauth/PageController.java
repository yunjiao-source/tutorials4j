package tutorials4j.springboot3.integration.googleauth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("/googleauth")
  public String twoFactorSetupPage() {
    return "googleauth";
  }
}
