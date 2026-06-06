package tutorials4j.framework.examples.captcha.captchafilter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("captcha-filter")
  public String captchafilter() {
    return "captcha/captchafilter/captcha-filter";
  }
}
