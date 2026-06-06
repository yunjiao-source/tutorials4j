package tutorials4j.framework.examples.crypto.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
public class ViewController {
  @GetMapping("cryptoapi")
  public String apicrypto() {
    return "crypto/api";
  }
}
