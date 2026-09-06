package tutorials4j.framework.examples.apikey;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorials4j.framework.auth.core.apikey.ApiKeyRateLimiter;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@RestController
public class ApiKeyController {

  @GetMapping("api-key")
  @ApiKeyRateLimiter(name = "demo", description = "测试API key限流")
  public String demo() {
    return "ok";
  }
}
