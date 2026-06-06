package tutorials4j.framework.examples.captcha.captchaendpoint;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  @GetMapping("hutool")
  public String hutool() {
    return "captcha/captchaendpoint/hutool";
  }

  @GetMapping("tianai-rotate")
  public String tianaiRotate() {
    return "captcha/captchaendpoint/tianai-rotate";
  }

  @GetMapping("tianai-word-image-click")
  public String tianaiWordImageClick() {
    return "captcha/captchaendpoint/tianai-word-image-click";
  }

  @GetMapping("tianai-slider")
  public String tianaiSliderDp() {
    return "captcha/captchaendpoint/tianai-slider";
  }
}
