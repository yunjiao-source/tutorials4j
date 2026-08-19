package tutorials4j.framework.examples.annotation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 注解示例页面控制器。
 *
 * <p>提供注解示例页面的视图跳转入口。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 跳转至幂等注解示例页面。
   *
   * @return 视图名称 {@code annotation/idempotent}
   */
  @GetMapping("idempotent")
  public String idempotent() {
    return "annotation/idempotent";
  }
}
