package tutorials4j.framework.examples.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.CaptchaCategory;
import tutorials4j.framework.captcha.CaptchaData;
import tutorials4j.framework.captcha.CaptchaService;
import tutorials4j.framework.captcha.CaptchaServiceFactory;
import tutorials4j.framework.captcha.hutool.web.CodeCaptchaReponse;
import tutorials4j.framework.captcha.hutool.web.CodeCaptchaRequest;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
@RequestMapping("captcha/hutool")
@RequiredArgsConstructor
public class HuttoolController {
  private final CaptchaServiceFactory factory;

  @GetMapping("/create")
  public CodeCaptchaReponse get(@RequestParam(name = "category") CaptchaCategory category) {
    CaptchaService service = factory.findService(category);
    CaptchaData data = service.draw();

    System.out.println("code=" + data.code());
    return CodeCaptchaReponse.of(data);
  }

  @PostMapping("/check")
  public Boolean post(@RequestBody CodeCaptchaRequest validate) {
    CaptchaService service = factory.findService(validate.getCategory());
    return service.verify(validate.getKey(), validate.getCode());
  }
}
