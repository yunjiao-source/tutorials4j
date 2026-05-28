package tutorials4j.springboot3.web.apicrypto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class ViewController {

  @GetMapping("/ui/apicrypto")
  public String apicrypto() {
    return "apicrypto";
  }
}
