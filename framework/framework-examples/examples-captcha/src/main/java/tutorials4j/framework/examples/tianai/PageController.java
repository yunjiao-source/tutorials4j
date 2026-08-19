package tutorials4j.framework.examples.tianai;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Tianai 验证码示例页面控制器。
 *
 * <p>负责将请求转发到 Tianai 验证码 WebSDK 示例页面。
 *
 * @author Yun Jiao
 */
@Controller
public class PageController {
  /**
   * 处理 {@code GET /tianai-websdk} 请求，跳转到 Tianai 验证码 WebSDK 示例页面。
   *
   * @return 视图名称 {@code tianai/tianai-websdk}
   */
  @GetMapping("tianai-websdk")
  public String tianaiWebsdk() {
    return "tianai/tianai-websdk";
  }
}
