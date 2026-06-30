package tutorials4j.framework.examples.tianai;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("tianai-websdk")
  public String tianaiWebsdk() {
    return "tianai/tianai-websdk";
  }
}
