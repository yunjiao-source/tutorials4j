package tutorials4j.framework.examples.captchafilter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 播客
 *
 * @author Yun Jiao
 */
@RestController
public class BlogController {

  @PostMapping("post")
  public String post(@RequestBody String content) {
    return "发布成功";
  }
}
