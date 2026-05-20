package tutorials4j.framework.captcha.web;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.CaptchaServiceFactory;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("captcha")
@RequiredArgsConstructor
public class CaptchaController {
  private final CaptchaServiceFactory factory;

  @GetMapping("/create")
  public Map<String, Object> get(@RequestParam(name = "category") CaptchaCategory category) {
    CaptchaService service = factory.findService(category);
    return service.draw();
  }

  @PostMapping("/check")
  public Boolean post(@RequestBody CaptchaRequest validate) {
    CaptchaService service = factory.findService(validate.getCategory());
    return service.verify(validate.getKey(), validate.getCode());
  }
}
