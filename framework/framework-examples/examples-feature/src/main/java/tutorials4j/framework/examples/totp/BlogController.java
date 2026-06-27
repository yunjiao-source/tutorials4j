package tutorials4j.framework.examples.totp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.web.core.annotation.TotpAuth;

/**
 * 播客
 *
 * @author Yun Jiao
 */
@RestController
public class BlogController {

  @TotpAuth
  @PostMapping("post")
  public String post(@RequestBody String content) {
    return "发布成功";
  }
}
