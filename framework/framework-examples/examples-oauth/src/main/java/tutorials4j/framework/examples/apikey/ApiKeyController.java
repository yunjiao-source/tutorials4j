package tutorials4j.framework.examples.apikey;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.auth.core.annotation.ApiKeyRateLimiter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
public class ApiKeyController {

  @GetMapping("api-key")
  @ApiKeyRateLimiter(name = "method1")
  public String method1() {
    return "ok";
  }
}
