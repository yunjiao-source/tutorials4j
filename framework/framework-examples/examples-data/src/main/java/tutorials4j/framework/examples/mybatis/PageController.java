package tutorials4j.framework.examples.mybatis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("curd")
  public String curd() {
    return "mybatis/curd";
  }
}
