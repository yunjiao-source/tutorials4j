package tutorials4j.framework.examples.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
@RequestMapping("page")
public class PageController {
  @GetMapping("hutool")
  public String showForm() {
    return "hutool"; // 对应 form.html
  }
}
