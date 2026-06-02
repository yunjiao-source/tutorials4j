package tutorials4j.framework.examples.apicrypto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面
 *
 * @author Yun Jiao
 */
@Controller
@RequestMapping("view")
public class ViewController {
  @GetMapping("apicrypto")
  public String apicrypto() {
    return "apicrypto";
  }
}
