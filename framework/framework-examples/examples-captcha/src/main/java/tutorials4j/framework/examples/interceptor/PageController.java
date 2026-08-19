package tutorials4j.framework.examples.interceptor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 验证码拦截器示例页面控制器。
 *
 * <p>提供拦截器方式接入验证码示例的前端页面入口，返回名为 {@code interceptor/interceptor} 的视图。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 返回拦截器验证码示例页面。
   *
   * @return 视图名称 {@code interceptor/interceptor}
   */
  @GetMapping("interceptor")
  public String interceptor() {
    return "interceptor/interceptor";
  }
}
