package tutorials4j.framework.examples.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面视图控制器，用于展示加密与摘要示例页面。
 *
 * @author Yun Jiao
 */
@Controller
@RequestMapping("page")
public class PageController {

  /** 返回加密示例页面视图。 */
  @GetMapping("crypto")
  public String crypto() {
    return "simple/crypto";
  }

  /** 返回摘要示例页面视图。 */
  @GetMapping("digest")
  public String digest() {
    return "simple/digest";
  }
}
