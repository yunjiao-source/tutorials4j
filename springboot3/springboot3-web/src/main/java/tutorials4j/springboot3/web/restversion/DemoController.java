package tutorials4j.springboot3.web.restversion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例
 *
 * @author yangyunjiao
 */
@RestController
@RequestMapping("/api")
public class DemoController {
  @GetMapping("/users")
  @ApiVersion(1.0)
  public String getUsersV1() {
    return "User data (Version 1)";
  }

  @GetMapping("/users")
  @ApiVersion(2.0)
  public String getUsersV2() {
    return "User data (Version 2)";
  }
}
