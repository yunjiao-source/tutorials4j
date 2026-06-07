package tutorials4j.framework.examples.signin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 签到页面
 *
 * @author Yun Jiao
 */
@Controller
@RequestMapping("sign-in")
public class PageController {
  @GetMapping("input")
  public String input() {
    return "signin/input";
  }

  @GetMapping("list")
  public String list() {
    return "signin/list";
  }
}
