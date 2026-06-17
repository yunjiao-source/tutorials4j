package tutorials4j.framework.examples.annotation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("idempotent")
  public String idempotent() {
    return "annotation/idempotent";
  }
}
