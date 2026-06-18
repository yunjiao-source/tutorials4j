package tutorials4j.framework.examples.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面视图控制器，用于展示缓存测试界面
 *
 * @author Yun Jiao
 */
@Controller
@RequestMapping("page")
public class PageController {

  @GetMapping("crypto")
  public String crypto() {
    return "simple/crypto";
  }

  @GetMapping("digest")
  public String digest() {
    return "simple/digest";
  }
}
