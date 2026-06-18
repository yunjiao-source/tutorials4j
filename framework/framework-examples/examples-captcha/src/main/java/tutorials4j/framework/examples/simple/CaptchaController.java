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
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {
  private final CaptchaServiceFactory factory;

  @GetMapping("create")
  public Map<String, Object> create(@RequestParam("category") CaptchaCategory category) {
    CaptchaService service = factory.findService(category);
    return service.draw();
  }
}
