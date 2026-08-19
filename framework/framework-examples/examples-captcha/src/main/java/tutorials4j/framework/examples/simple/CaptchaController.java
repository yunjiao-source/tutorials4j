package tutorials4j.framework.examples.simple;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.support.CaptchaCategory;
import tutorials4j.framework.captcha.support.CaptchaService;
import tutorials4j.framework.captcha.support.CaptchaServiceFactory;

/**
 * 验证码示例控制器。
 *
 * <p>演示根据验证码类别从 {@link CaptchaServiceFactory} 中获取对应的验证码服务， 并生成验证码图片数据。
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {
  private final CaptchaServiceFactory factory;

  /**
   * 创建指定类别的验证码。
   *
   * @param category 验证码类别
   * @return 验证码绘制结果（包含图片及验证信息）
   */
  @GetMapping("create")
  public Map<String, Object> create(@RequestParam("category") CaptchaCategory category) {
    CaptchaService service = factory.findService(category);
    return service.draw();
  }
}
