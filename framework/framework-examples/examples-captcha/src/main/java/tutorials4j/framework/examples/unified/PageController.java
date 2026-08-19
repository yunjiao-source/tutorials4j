package tutorials4j.framework.examples.unified;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 统一验证码示例页面控制器。
 *
 * <p>负责将请求转发到统一接入方式（Hutool、Tianai 等）的验证码示例页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 处理 {@code GET /hutool} 请求，跳转到 Hutool 验证码示例页面。
   *
   * @return 视图名称 {@code unified/hutool}
   */
  @GetMapping("hutool")
  public String hutool() {
    return "unified/hutool";
  }

  /**
   * 处理 {@code GET /tianai-rotate} 请求，跳转到旋转验证码示例页面。
   *
   * @return 视图名称 {@code unified/tianai-rotate}
   */
  @GetMapping("tianai-rotate")
  public String tianaiRotate() {
    return "unified/tianai-rotate";
  }

  /**
   * 处理 {@code GET /tianai-word-image-click} 请求，跳转到文字点选验证码示例页面。
   *
   * @return 视图名称 {@code unified/tianai-word-image-click}
   */
  @GetMapping("tianai-word-image-click")
  public String tianaiWordImageClick() {
    return "unified/tianai-word-image-click";
  }

  /**
   * 处理 {@code GET /tianai-slider} 请求，跳转到滑块验证码示例页面。
   *
   * @return 视图名称 {@code unified/tianai-slider}
   */
  @GetMapping("tianai-slider")
  public String tianaiSliderDp() {
    return "unified/tianai-slider";
  }
}
