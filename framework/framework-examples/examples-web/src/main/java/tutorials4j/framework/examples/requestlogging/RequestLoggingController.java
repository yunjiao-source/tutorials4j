package tutorials4j.framework.examples.requestlogging;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 请求日志输出
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("request-logging")
public class RequestLoggingController {
  @GetMapping("/hello")
  public String sayHello(@RequestParam(value = "name", required = false) String name) {
    return "Hello " + (name != null ? name : "World") + "!";
  }

  // POST 请求示例：接收 JSON 请求体
  @PostMapping("/user")
  public User createUser(@RequestBody User user) {
    // 模拟业务处理，比如保存用户
    user.setId(100L);
    return user;
  }

  // 一个简单的实体类
  @Data
  public static class User {
    private Long id;
    private String username;
    private String email;
  }
}
