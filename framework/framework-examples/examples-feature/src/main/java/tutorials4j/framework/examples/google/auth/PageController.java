package tutorials4j.framework.examples.google.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("auth")
  public String googleAuthPage() {
    return "google/auth";
  }

  @GetMapping("blog")
  public String blog() {
    return "google/blog";
  }
}
