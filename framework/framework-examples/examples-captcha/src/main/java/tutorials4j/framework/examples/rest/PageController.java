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
  public String hutool() {
    return "hutool";
  }

  @GetMapping("tianai-websdk")
  public String tianaiWebsdk() {
    return "tianai-websdk";
  }

  @GetMapping("tianai-rotate")
  public String tianaiRotate() {
    return "tianai-rotate";
  }

  @GetMapping("tianai-word-image-click")
  public String tianaiWordImageClick() {
    return "tianai-word-image-click";
  }

  @GetMapping("tianai-slider-dp")
  public String tianaiSliderDp() {
    return "tianai-slider-dp";
  }

  @GetMapping("tianai-slider-yb")
  public String tianaiSliderYb() {
    return "tianai-slider-yb";
  }
}
