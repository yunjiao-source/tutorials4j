package tutorials4j.framework.examples.signin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 签到功能页面控制器，提供签到录入与签到列表页面。
 *
 * @author Yun Jiao
 */
@Controller
@RequestMapping("sign-in")
public class PageController {
  /**
   * 返回签到录入页面视图。
   *
   * @return 视图名
   */
  @GetMapping("input")
  public String input() {
    return "signin/input";
  }

  /**
   * 返回签到列表页面视图。
   *
   * @return 视图名
   */
  @GetMapping("list")
  public String list() {
    return "signin/list";
  }
}
