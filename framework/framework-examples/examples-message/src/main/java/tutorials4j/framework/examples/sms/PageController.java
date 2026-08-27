package tutorials4j.framework.examples.sms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("sms")
  public String sms() {
    return "sms/demo";
  }
}
