package tutorials4j.framework.examples.unified;

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
    return "unified/hutool";
  }

  @GetMapping("tianai-rotate")
  public String tianaiRotate() {
    return "unified/tianai-rotate";
  }

  @GetMapping("tianai-word-image-click")
  public String tianaiWordImageClick() {
    return "unified/tianai-word-image-click";
  }

  @GetMapping("tianai-slider")
  public String tianaiSliderDp() {
    return "unified/tianai-slider";
  }
}
