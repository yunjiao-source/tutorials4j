package tutorials4j.framework.examples.validation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 校验示例页面控制器
 *
 * <p>提供校验演示相关页面的跳转入口。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /** 跳转到校验演示页面 */
  @GetMapping("demo")
  public String demo() {
    return "validation/demo";
  }

  /** 跳转到注册示例页面 */
  @GetMapping("register")
  public String register() {
    return "validation/register";
  }
}
