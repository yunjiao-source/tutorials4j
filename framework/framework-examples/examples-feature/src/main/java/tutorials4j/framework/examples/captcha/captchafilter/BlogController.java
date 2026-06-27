package tutorials4j.framework.examples.captcha.captchafilter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.captcha.annotation.CaptchaAuth;

/**
 * 播客
 *
 * @author Yun Jiao
 */
@RestController
public class BlogController {

  @CaptchaAuth
  @PostMapping("post")
  public String post(@RequestBody String content) {
    return "发布成功";
  }
}
