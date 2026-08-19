package tutorials4j.framework.examples.requestlogging;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 请求日志示例控制器。
 *
 * <p>提供 GET 与 POST 示例接口，用于演示请求日志过滤器对请求与响应的记录效果。
 *
 * @author yangyunjiao
 */
@Slf4j
@RestController
@RequestMapping("request-logging")
public class RequestLoggingController {
  /**
   * 简单的 GET 接口，返回问候语。
   *
   * @param name 可选的姓名参数
   * @return 拼接后的问候语字符串
   */
  @GetMapping("/hello")
  public String sayHello(@RequestParam(value = "name", required = false) String name) {
    return "Hello " + (name != null ? name : "World") + "!";
  }

  // POST 请求示例：接收 JSON 请求体
  /**
   * 模拟创建用户的 POST 接口。
   *
   * @param user 请求体中的用户信息
   * @return 设置 ID 后的用户信息
   */
  @PostMapping("/user")
  public User createUser(@RequestBody User user) {
    // 模拟业务处理，比如保存用户
    user.setId(100L);
    return user;
  }

  // 一个简单的实体类
  /** 用户实体，用于接收和返回用户信息。 */
  @Data
  public static class User {
    private Long id;
    private String username;
    private String email;
  }
}
