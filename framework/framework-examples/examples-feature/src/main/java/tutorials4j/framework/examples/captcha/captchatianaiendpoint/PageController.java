package tutorials4j.framework.examples.captcha.captchatianaiendpoint;

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
    return "captchatianaiendpoint/tianai-websdk";
  }
}
