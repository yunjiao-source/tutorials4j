package tutorials4j.framework.examples.satoken;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Yun Jiao
 */
@Controller
public class PageController {

  @GetMapping("api-key")
  public String apiKey() {
    return "sa-token/api-key";
  }
}
